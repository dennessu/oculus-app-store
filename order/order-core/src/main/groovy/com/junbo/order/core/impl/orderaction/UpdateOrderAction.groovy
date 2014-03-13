package com.junbo.order.core.impl.orderaction

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 3/7/14.
 */
@Component('updateOrderAction')
@CompileStatic
class UpdateOrderAction implements Action {

    @Resource(name = 'orderRepository')
    OrderRepository orderRepository

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Override
    @OrderEventAwareBefore
    @OrderEventAwareAfter
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        // Save Order
        // Fetch Preorder Info from catalog
        builder.getOffers(context.orderServiceContext).syncThen { List<Offer> ofs ->

            // TODO get and build preorder info
            def orderWithId = orderRepository.updateOrder(context.orderServiceContext.order, false)
            context.orderServiceContext.order = orderWithId

            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
        }
    }
}
