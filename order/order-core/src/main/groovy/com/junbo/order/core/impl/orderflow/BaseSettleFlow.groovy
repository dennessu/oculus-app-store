package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.OrderFlow
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.context.BaseContext
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
        BaseContext context = new BaseContext()
        context.setOrderServiceContext(order)
        Promise<BaseContext> lastPromise = Promise.pure(context)
        orderActions.each { OrderAction action ->
            lastPromise = lastPromise.then {
                action.execute(context)
            }
        }
        lastPromise.syncThen { BaseContext result ->
            [((BaseContext)result).orderServiceContext.order]
        }
    }
}
