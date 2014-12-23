package com.junbo.order.core.impl.orderaction
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

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
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
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

                if (eventMap.containsKey('callbackData')) {
                    String data = eventMap.get('callbackData');

                    return facadeContainer.paymentFacade.postPaymentProperties(data).recover {
                        Throwable throwable ->
                            LOGGER.error('name=Post_Payment_Callback_Error', throwable)
                            throw throwable
                    }.then {
                        LOGGER.info('name=Post_Payment_Callback, order event map=' + eventMap)
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
