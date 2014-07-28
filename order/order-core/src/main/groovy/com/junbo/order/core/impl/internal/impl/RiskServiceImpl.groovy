package com.junbo.order.core.impl.internal.impl

import com.junbo.common.id.PIType
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.impl.internal.RiskReviewResult
import com.junbo.order.core.impl.internal.RiskService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.BillingHistory
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.payment.spec.model.PaymentInstrument
import com.kount.ris.Inquiry
import com.kount.ris.KountRisClient
import com.kount.ris.Response
import com.kount.ris.Update
import com.kount.ris.util.AuthorizationStatus
import com.kount.ris.util.CartItem
import com.kount.ris.util.MerchantAcknowledgment
import com.kount.ris.util.RisException
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by xmchen on 14-6-23.
 */
@CompileStatic
@TypeChecked
class RiskServiceImpl implements RiskService {

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Autowired
    OrderRepositoryFacade orderRepository

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    int merchantId

    String kountUrl

    String kountKeyFileName

    String kountKeyFilePass

    @Required
    void setMerchantId(int merchantId) {
        this.merchantId = merchantId
    }

    @Required
    void setKountUrl(String kountUrl) {
        this.kountUrl = kountUrl
    }

    @Required
    void setKountKeyFileName(String kountKeyFileName) {
        this.kountKeyFileName = kountKeyFileName
    }

    @Required
    void setKountKeyFilePass(String kountKeyFilePass) {
        this.kountKeyFilePass = kountKeyFilePass
    }

    KountRisClient kountRisClient = null

    private final static String NO_EMAIL = "noemail@kount.com"

    private static final Logger LOGGER = LoggerFactory.getLogger(RiskServiceImpl)

    @Override
    @Transactional
    Promise<RiskReviewResult> reviewOrder(OrderServiceContext orderContext) {
        def order = orderContext.order
        def ip = orderContext.apiContext.userIp

        if (ip == null || ip.isEmpty()) {
            throw AppErrors.INSTANCE.orderRiskReviewError("ip address missing").exception()
        }

        Inquiry q = new Inquiry();
        q.setMerchantId(merchantId)
        q.setSessionId(UUID.randomUUID().toString().replaceAll('-', ''))
        q.setOrderNumber(order.id.toString())
        q.setIpAddress(ip)
        q.setAuthorizationStatus(AuthorizationStatus.APPROVED)
        q.setMerchantAcknowledgment(MerchantAcknowledgment.YES)
        q.setWebsite("DEFAULT")
        int baseUnit = 0

        return orderServiceContextBuilder.getCurrency(orderContext).then { Currency currency ->
            baseUnit = Math.pow(10, currency.numberAfterDecimal).intValue();

            q.setCurrency(order.currency.value)
            q.setTotal((int)(order.totalAmount * baseUnit))

            return Promise.pure(null)
        }.then {
            return orderServiceContextBuilder.getEmail(orderContext).then { String email ->
                if (email == null) {
                    q.setEmail(NO_EMAIL)
                } else {
                    q.setEmail(email)
                }

                return Promise.pure(null)
            }
        }.then {
            return builder.getOffers(orderContext).then {
                Collection<CartItem> cart = new Vector<CartItem>()

                order.orderItems?.each { OrderItem orderItem ->
                    def offer = orderContext.offersMap[orderItem.offer]
                    String type = orderItem.type
                    String id = orderItem.offer
                    String desc = offer.locales['DEFAULT']?.shortDescription
                    if (desc !=  null && desc.length() > 256) {
                        desc = desc.substring(0, 255)
                    }
                    CartItem cartItem = new CartItem(type, id, desc,
                            orderItem.quantity, (int)(orderItem.unitPrice * baseUnit))
                    cart.add(cartItem)
                }
                q.setCart(cart)

                return Promise.pure(null)
            }
        }.then {
            return orderServiceContextBuilder.getPaymentInstruments(orderContext).then {
                if (orderContext.paymentInstruments != null && orderContext.paymentInstruments.size() > 0) {
                    PaymentInstrument pi = orderContext.paymentInstruments.get(0)
                    switch (PIType.get(pi.type)) {
                        case PIType.CREDITCARD:
                            q.setPayment("CARD", pi.accountNum)
                            break;
                        case PIType.STOREDVALUE:
                            q.setPayment("GIFT", pi.id.toString())
                            break;
                        case PIType.PAYPAL:
                        case PIType.OTHERS:
                            q.setPayment("NONE", null)
                            break
                    }
                }

                return Promise.pure(null)
            }
        }.then {
            return orderServiceContextBuilder.getBillingAddresses(orderContext).then {
                if (orderContext.billingAddresses != null && orderContext.billingAddresses.size() > 0) {
                    Address billingAddress = orderContext.billingAddresses.get(0);
                    com.kount.ris.util.Address address = new com.kount.ris.util.Address(
                            billingAddress.street1,
                            billingAddress.street2,
                            billingAddress.city,
                            billingAddress.subCountry,
                            billingAddress.postalCode,
                            billingAddress.countryId.value
                    )
                    q.setBillingAddress(address)
                }

                return Promise.pure(null)
            }
        }.then {
            return orderServiceContextBuilder.getShippingAddress(orderContext).then {
                if (orderContext.shippingAddress != null) {
                    com.kount.ris.util.Address address = new com.kount.ris.util.Address(
                            orderContext.shippingAddress.street1,
                            orderContext.shippingAddress.street2,
                            orderContext.shippingAddress.city,
                            orderContext.shippingAddress.subCountry,
                            orderContext.shippingAddress.postalCode,
                            orderContext.shippingAddress.countryId.value
                    )
                    q.setBillingAddress(address)
                }

                return Promise.pure(null)
            }
        }.then {

            try {
                LOGGER.info('name=Review_Order_Request, ' + q.getParams().toString())
                Response r = getKountRisClient().process(q);
                LOGGER.info('name=Review_Order_Response, ' + r.toString())

                if (order.properties == null) {
                    order.setProperties(new HashMap<String, String>())
                }

                order.properties.put('riskTransactionId', r.getTransactionId())
                if (r.auto == 'D') {
                    order.setStatus(OrderStatus.RISK_REJECT.name())
                    orderRepository.updateOrder(order, true, false, null)

                    return Promise.pure(RiskReviewResult.REJECT)
                } else {
                    orderRepository.updateOrder(order, true, false, null)
                    return Promise.pure(RiskReviewResult.APPROVED)
                }
            } catch (RisException re) {
                LOGGER.error('name=Review_Order_Exception', re)
                throw AppErrors.INSTANCE.orderRiskReviewError(re.getMessage()).exception()
            }
        }
    }

    @Override
    Promise<Void> updateReview(OrderServiceContext orderContext) {
        def order = orderContext.order

        if (order.properties != null && order.properties.containsKey('riskTransactionId')) {
            Update update = new Update()
            update.setMerchantId(merchantId)
            update.setSessionId(UUID.randomUUID().toString().replaceAll('-', ''))
            update.setTransactionId(order.properties.get('riskTransactionId'))

            if (order.properties.containsKey('payerId')) {
                update.setPayment("PYPL", order.properties.get('payerId'))
            }

            Boolean failed = order.billingHistories.any{ BillingHistory billingHistory ->
                (billingHistory.billingEvent == BillingAction.AUTHORIZE.name() ||
                        billingHistory.billingEvent == BillingAction.CHARGE.name()) &&
                        billingHistory.success == false
            }
            update.setMerchantAcknowledgment(MerchantAcknowledgment.YES)
            if (failed) {
                update.setAuthorizationStatus(AuthorizationStatus.DECLINED)
            }

            try {
                LOGGER.info('name=Update_Review_Request, ' + update.getParams().toString())
                Response r = getKountRisClient().process(update);
                LOGGER.info('name=Update_Review_Response, ' + r.toString())
            } catch (RisException re) {
                LOGGER.error('name=Update_Review_Exception', re)
                throw AppErrors.INSTANCE.orderRiskReviewError(re.getMessage()).exception()
            }
        }

        return Promise.pure(null)
    }

    private KountRisClient getKountRisClient() {
        if (kountRisClient == null) {
            InputStream stream = RiskServiceImpl.classLoader.getResourceAsStream(kountKeyFileName)
            kountRisClient = new KountRisClient(kountKeyFilePass, kountUrl, stream)
        }
        return kountRisClient
    }
}
