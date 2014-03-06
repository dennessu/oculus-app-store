package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.OrderFlow
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import org.springframework.util.Assert

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
abstract class BaseSettleFlow implements OrderFlow {

    @Override
    abstract UUID getName()

    @Override
    Promise<List<Order>> execute(OrderServiceContext order) {
        return Promise.pure([])
    }

    protected Promise<List<Order>> execute(OrderServiceContext order, List<OrderAction> orderActions) {
        Assert.notEmpty(orderActions, 'orderActions could not be empty')
        OrderActionContext context = new OrderActionContext()
        context.setOrderServiceContext(order)
        Promise<OrderActionContext> lastPromise = Promise.pure(context)
        orderActions.each { OrderAction action ->
            lastPromise = lastPromise.then {
                action.execute(context)
            }
        }
        lastPromise.syncThen { OrderActionContext result ->
            [((OrderActionContext)result).orderServiceContext.order]
        }
    }
}
