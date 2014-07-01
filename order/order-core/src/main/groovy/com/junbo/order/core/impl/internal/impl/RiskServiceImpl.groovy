package com.junbo.order.core.impl.internal.impl

import com.junbo.common.id.PIType
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.langur.core.promise.Promise
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.internal.RiskReviewResult
import com.junbo.order.core.impl.internal.RiskService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.payment.spec.model.PaymentInstrument
import com.kount.ris.Inquiry
import com.kount.ris.KountRisClient
import com.kount.ris.Response
import com.kount.ris.util.CartItem
import com.kount.ris.util.MerchantAcknowledgment
import com.kount.ris.util.RisException
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Service

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-23.
 */
@CompileStatic
@TypeChecked
class RiskServiceImpl implements RiskService {

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Autowired
    OrderRepositoryFacade orderRepository

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

    private final static String NO_EMAIL = "noemail@kount.com"

    private static final Logger LOGGER = LoggerFactory.getLogger(RiskServiceImpl)

    @Override
    Promise<RiskReviewResult> reviewOrder(OrderServiceContext orderContext) {
        def order = orderContext.order
        def user = orderContext.user
        def ip = orderContext.apiContext.userIp

        if (ip == null || ip.isEmpty()) {
            throw AppErrors.INSTANCE.orderRiskReviewError("ip address missing").exception()
        }

        UserPersonalInfoId emailId = null
        for (UserPersonalInfoLink link : user.emails) {
            if (link.isDefault) {
                emailId = link.value
            }
        }

        return facadeContainer.identityFacade.getCurrency(order.currency.value).then { Currency currency ->

            int baseUnit = Math.pow(10, currency.numberAfterDecimal).intValue();

            Inquiry q = new Inquiry();
            q.setMerchantId(merchantId)
            q.setSessionId(UUID.randomUUID().toString().replaceAll('-', ''))
            q.setOrderNumber(order.id.toString())
            q.setCurrency(order.currency.value)
            q.setTotal((int)(order.totalAmount * baseUnit))
            q.setIpAddress(ip)
            q.setMerchantAcknowledgment(MerchantAcknowledgment.YES)

            return facadeContainer.identityFacade.getEmail(emailId).recover { Throwable throwable ->
                LOGGER.error("name=Error_Review_Order_Get_Email", throwable)
                return Promise.pure(null)
            }.then { String email ->
                if (email == null) {
                    q.setEmail(NO_EMAIL)
                } else {
                    q.setEmail(email)
                }

                return builder.getOffers(orderContext).then {
                    Collection<CartItem> cart = new Vector<CartItem>()

                    order.orderItems?.each { OrderItem orderItem ->
                        def offer = orderContext.offersMap[orderItem.offer]
                        String type = orderItem.type
                        String id = orderItem.offer
                        String desc = offer.catalogOfferRevision.locales['DEFAULT']?.shortDescription
                        if (desc !=  null && desc.length() > 256) {
                            desc = desc.substring(0, 255)
                        }
                        CartItem cartItem = new CartItem(type, id, desc,
                                orderItem.quantity, (int)(orderItem.unitPrice * baseUnit))
                        cart.add(cartItem)
                    }
                    q.setCart(cart)

                    if (orderContext.paymentInstruments != null && orderContext.paymentInstruments.size() > 0) {
                        PaymentInstrument pi = orderContext.paymentInstruments.get(0)
                        switch (PIType.get(pi.type)) {
                            case PIType.CREDITCARD:
                                q.setPayment("CARD", pi.accountNum)
                                break;
                            case PIType.PAYPAL:
                                q.setPayment("PYPL", "")
                                break;
                            case PIType.STOREDVALUE:
                                q.setPayment("GIFT", pi.id.toString())
                                break;
                            case PIType.OTHERS:
                                q.setPayment("NONE", "")
                                break
                        }
                    }
                    q.setWebsite("DEFAULT")

                    InputStream stream = RiskServiceImpl.classLoader.getResourceAsStream(kountKeyFileName)
                    KountRisClient kountRisClient = new KountRisClient(kountKeyFilePass, kountUrl, stream)

                    try {
                        LOGGER.info('name=Review_Order_Request, ' + q.getParams().toString())
                        Response r = kountRisClient.process(q);
                        LOGGER.info('name=Review_Order_Response, ' + r.toString())
                        orderContext.riskTransactionId = r.getTransactionId()

                        if (r.auto == 'D') {
                            order.setStatus(OrderStatus.RISK_REJECT.name())
                            orderRepository.updateOrder(order, true, false, null)

                            return Promise.pure(RiskReviewResult.REJECT)
                        } else {
                            return Promise.pure(RiskReviewResult.APPROVED)
                        }
                    } catch (RisException re) {
                        LOGGER.error('name=Review_Order_Exception', re)
                        throw AppErrors.INSTANCE.orderRiskReviewError(re.getMessage()).exception()
                    }
                }
            }
        }
    }
}
