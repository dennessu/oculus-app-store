package com.junbo.order.core.impl.orderaction
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.repo.OrderRepository
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('createOrderAction')
class SaveOrderAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderRepository')
    OrderRepository repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder
    boolean newOrder = true

    @Override
    @com.junbo.order.core.annotation.OrderEventAwareBefore
    @com.junbo.order.core.annotation.OrderEventAwareAfter
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        // Save Order
        // Fetch Preorder Info from catalog
        builder.getOffers(context.orderServiceContext).syncThen { List<Offer> ofs ->
            context.orderServiceContext.order.status = OrderStatus.OPEN
            def orderWithId = repo.createOrder(context.orderServiceContext.order)
            context.orderServiceContext.order = orderWithId

            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
        }
    }
}
