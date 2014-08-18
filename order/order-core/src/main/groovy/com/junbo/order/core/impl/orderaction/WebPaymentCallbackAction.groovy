package com.junbo.order.core.impl.orderaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.common.CommonUtil
import com.junbo.payment.spec.model.PaymentCallbackParams
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by xmchen on 14-7-4.
 */
@CompileStatic
@TypeChecked
@Component('webPaymentCallbackAction')
class WebPaymentCallbackAction extends BaseOrderEventAwareAction {

    @Autowired
    OrderRepositoryFacade orderRepository

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    ObjectMapper objectMapper = new ObjectMapper()

    private static final Logger LOGGER = LoggerFactory.getLogger(WebPaymentCallbackAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def orderEvent = context.orderServiceContext.orderEvent

        if (orderEvent.properties != null && !orderEvent.properties.isEmpty()) {
            try {
                Map<String, String> eventMap = objectMapper.readValue(orderEvent.properties, Map)

                if (order.properties == null || order.properties.isEmpty()) {
                    order.setProperties(eventMap)
                } else {
                    order.properties.putAll(eventMap)
                    orderRepository.updateOrder(order, true, false, null)
                }

                if (eventMap.containsKey('paymentId')) {
                    Long paymentId = CommonUtil.decode(eventMap.get('paymentId'));

                    PaymentCallbackParams params = new PaymentCallbackParams()
                    if (eventMap.containsKey('token')) {
                        params.setToken(eventMap.get('token'))
                    }
                    if (eventMap.containsKey('payerId')) {
                        params.setPayerID(eventMap.get('payerId'))
                    }
                    if (eventMap.containsKey('authResult')) {
                        params.setAuthResult(eventMap.get('authResult'))
                    }
                    if (eventMap.containsKey('pspReference')) {
                        params.setPspReference(eventMap.get('pspReference'))
                    }
                    if (eventMap.containsKey('merchantReference')) {
                        params.setMerchantReference(eventMap.get('merchantReference'))
                    }
                    if (eventMap.containsKey('skinCode')) {
                        params.setSkinCode(eventMap.get('skinCode'))
                    }
                    if (eventMap.containsKey('merchantReturnData')) {
                        params.setSkinCode(eventMap.get('merchantReturnData'))
                    }
                    if (eventMap.containsKey('merchantSig')) {
                        params.setMerchantSig(eventMap.get('merchantSig'))
                    }

                    return facadeContainer.paymentFacade.postPaymentProperties(paymentId, params).recover {
                        Throwable throwable ->
                            LOGGER.error('name=Post_Payment_Callback_Error', throwable)
                            throw AppErrors.INSTANCE.paymentConnectionError().exception()
                    }.then {
                        LOGGER.info('name=Post_Payment_Callback, paymentId=' + paymentId +
                                ', order event map=' + eventMap)
                        return Promise.pure(null)
                    }
                }
            } catch (IOException ex) {
                LOGGER.error('name=WebPaymentCallback_ParseJson_Error', ex)
            }
        }

        return Promise.pure(null)
    }
}
