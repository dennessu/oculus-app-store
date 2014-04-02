package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PreorderInfo
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
    boolean updateOnlyOrder = false

    @Override
    @OrderEventAwareBefore(action = 'SaveOrderAction')
    @OrderEventAwareAfter(action = 'SaveOrderAction')
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        order.status = OrderStatusBuilder.buildOrderStatus(order,
                order.id == null ? (List<OrderEvent>)[] : repo.getOrderEvents(order.id.value))
        // Save Order
        // Fetch Preorder Info from catalog
        builder.getOffers(context.orderServiceContext).syncThen { List<OrderOffer> ofs ->
            fillPreorderInfo(ofs, order)
            def orderWithId = newOrder ? repo.createOrder(context.orderServiceContext.order) :
                    repo.updateOrder(order, updateOnlyOrder)
            order = orderWithId
            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
        }
    }

    private void fillPreorderInfo(List<OrderOffer> ofs, Order order) {
        Date now = new Date()
        def preorderInfo = null
        ofs.each { OrderOffer orderOffer ->
            // TODO: update preorder detection with Catalog component
            Date releaseDate = orderOffer.catalogOffer.properties.get('releaseTime') as Date
            if (releaseDate?.after(now)) {
                // pre-order
                preorderInfo = new PreorderInfo()
                preorderInfo.releaseTime = releaseDate
                preorderInfo.billingTime = orderOffer.catalogOffer.properties.get('billingTime') as Date
                preorderInfo.preNotificationTime = orderOffer.catalogOffer.properties.get('preNotificationTime') as Date
            }
            order.orderItems.each { OrderItem orderItem ->
                if (orderItem.offer.value == orderOffer.catalogOffer.id) {
                    orderItem.preorderInfo = preorderInfo as PreorderInfo
                }
            }
        }
    }
}
