package com.junbo.order.core.impl.internal.impl

import com.junbo.common.id.PIType
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.langur.core.promise.Promise
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.internal.RiskService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.model.OrderItem
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
import org.springframework.stereotype.Service

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-23.
 */
@CompileStatic
@TypeChecked
@Service('riskService')
class RiskServiceImpl implements RiskService {

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    int merchantId = 600900

    String kountUrl = 'https://risk.test.kount.net'

    /*@Required
    void setMerchantId(int merchantId) {
        this.merchantId = merchantId
    }

    @Required
    void setKountUrl(int kountUrl) {
        this.kountUrl = kountUrl
    }*/

    private final static String NO_EMAIL = "noemail@kount.com"

    private static final Logger LOGGER = LoggerFactory.getLogger(RiskServiceImpl)

    @Override
    Promise<Void> reviewOrder(OrderServiceContext orderContext) {
        def order = orderContext.order
        def user = orderContext.user
        def ip = orderContext.apiContext.userIp

        UserPersonalInfoId emailId = null
        for (UserPersonalInfoLink link : user.emails) {
            if (link.isDefault) {
                emailId = link.value
            }
        }

        return facadeContainer.identityFacade.getCurrency(order.currency.value).syncThen { Currency currency ->

            int baseUnit = Math.pow(10, currency.numberAfterDecimal).intValue();

            Inquiry q = new Inquiry();
            q.setMerchantId(merchantId)
            //todo: client should pass in the session id for kount
            q.setSessionId("")
            q.setCurrency(order.currency.value)
            q.setTotal((int)(order.totalAmount * baseUnit))
            q.setIpAddress(ip)
            q.setMerchantAcknowledgment(MerchantAcknowledgment.YES)

            return facadeContainer.identityFacade.getEmail(emailId).syncRecover { Throwable throwable ->
                LOGGER.error("name=Error_Review_Order_Get_Email", throwable)
                return null
            }.syncThen { String email ->
                if (email == null) {
                    q.setEmail(NO_EMAIL)
                } else {
                    q.setEmail(email)
                }

                return builder.getOffers(orderContext).syncThen {
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
                    q.setWebsite("OCULUS")

                    KountRisClient kountRisClient = new KountRisClient('#Bugsfor$', kountUrl,
                        'risk_kount_test.p12')

                    try {
                        LOGGER.info('name=Review_Order_Request, ' + q.toString())
                        Response r = kountRisClient.process(q);
                        LOGGER.info('name=Review_Order_Response, ' + r.toString())
                        orderContext.riskTransactionId = r.getTransactionId();
                    } catch (RisException re) {
                        LOGGER.error('name=Review_Order_Exception', re)
                    }
                }
            }
        }
    }
}
