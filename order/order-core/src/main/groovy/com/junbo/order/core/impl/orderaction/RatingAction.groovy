package com.junbo.order.core.impl.orderaction
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
@TypeChecked
class RatingAction implements Action {

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        if (order.honoredTime == null) {
            order.honoredTime = new Date()
            order.orderItems.each { OrderItem oi ->
                oi.honoredTime = order.honoredTime
            }
        }
        return orderInternalService.rateOrder(order).recover { Throwable ex ->
            LOGGER.error('name=Rating_Action_Error', ex)
            if (ex instanceof AppErrorException) {
                throw ex
            }
            throw AppErrors.INSTANCE.unexpectedError().exception()
        }.syncThen { Order o ->
            if (o == null) {
                LOGGER.error('name=Rating_Action_Error_Null')
                throw AppErrors.INSTANCE.ratingResultInvalid().exception()
            }
            return null
        }
    }
}