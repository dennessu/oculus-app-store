package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.spec.model.EventStatus
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
class RatingAction implements OrderAction<BaseContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingAction)

    @Override
    Promise<BaseContext> execute(BaseContext context) {
        def order = context.orderServiceContext.order
        def serviceContext = context.orderServiceContext
        return serviceContext.ratingFacade.
                rateOrder(order, serviceContext?.shippingAddress?.country).syncRecover {  Throwable throwable ->
            LOGGER.error('name=Order_Rating_Error', throwable)
            return null
        }.syncThen { OrderRatingRequest ratingResult ->
            if (ratingResult == null) {
                serviceContext.orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(order.id,
                                com.junbo.order.spec.model.OrderAction.RATE, EventStatus.ERROR))
            } else {
                // todo handle rating violation
                CoreBuilder.fillRatingInfo(order, ratingResult)
                EventStatus ratingStatus = EventStatus.COMPLETED
                serviceContext.orderRepository.createOrderEvent(
                        CoreBuilder.buildOrderEvent(order.id,
                                com.junbo.order.spec.model.OrderAction.RATE, ratingStatus))
                return context
            }
        }
    }
}
