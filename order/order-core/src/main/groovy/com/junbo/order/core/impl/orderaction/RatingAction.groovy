package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class RatingAction implements Action {

    @Resource(name = 'ratingFacade')
    RatingFacade ratingFacade

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        return ratingFacade.rateOrder(order).syncRecover {
            Throwable throwable ->
            LOGGER.error('name=Order_Rating_Error', throwable)
            return null
        }.syncThen { OrderRatingRequest ratingResult ->
            // todo handle rating violation
            CoreBuilder.fillRatingInfo(order, ratingResult)
           //  no need to log event for rating
           //  EventStatus ratingStatus = EventStatus.COMPLETED
           //  serviceContext.orderRepository.createOrderEvent(
           //         CoreBuilder.buildOrderEvent(order.id,
           //                 com.junbo.order.spec.model.OrderAction.RATE, ratingStatus))
            return null
        }
    }
}
