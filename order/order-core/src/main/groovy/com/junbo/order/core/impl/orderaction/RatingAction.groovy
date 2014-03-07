package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.EventStatus
import com.junbo.order.spec.model.OrderActionType
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class RatingAction implements Action {

    @Autowired
    RatingFacade ratingFacade

    @Autowired
    OrderRepository orderRepository

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
            if (ratingResult == null) {
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.RATE,
                                EventStatus.ERROR))
            } else {
                // todo handle rating violation
                CoreBuilder.fillRatingInfo(order, ratingResult)
                EventStatus ratingStatus = EventStatus.COMPLETED
                orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(
                                order.id,
                                OrderActionType.RATE,
                                ratingStatus))
                return context
            }
            return ActionUtils.DEFAULT_RESULT
        }
    }
}
