package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.OrderStatusBuilder
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.enums.EventStatus
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
    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade repo
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    boolean newOrder = true
    boolean updateOnlyOrder = false

    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        order.status = OrderStatusBuilder.buildOrderStatus(order)
        // Save Order
        return builder.getOffers(context.orderServiceContext).syncThen { List<Offer> ofs ->
            def orderWithId = newOrder ? repo.createOrder(context.orderServiceContext.order) :
                    repo.updateOrder(order, updateOnlyOrder, false, null)
            order = orderWithId
            return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
        }
    }
}
