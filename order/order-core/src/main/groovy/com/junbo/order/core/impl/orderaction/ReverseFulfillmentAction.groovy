package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.transaction.annotation.Transactional
/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
@TypeChecked
class ReverseFulfillmentAction extends BaseOrderEventAwareAction {

    @Autowired
    @Qualifier('orderInternalService')
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseFulfillmentAction)

    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        def order = serviceContext.order

        return orderInternalService.reverseFulfillment(order).then { Boolean completed ->
            EventStatus orderEventStatus = EventStatus.COMPLETED
            if (!completed) {
                orderEventStatus = EventStatus.FAILED
            }
            return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(context, orderEventStatus))
        }
    }
}
