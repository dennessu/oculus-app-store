package com.junbo.order.core.mock

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.OrderItem

import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * Created by fzhang on 14-2-26.
 */
class MockAction implements OrderAction<OrderActionContext> {

    private OrderItem orderItem

    private static final ListeningExecutorService SERVICE = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(10))

    MockAction() {
    }

    MockAction(OrderItem orderItem) {
        this.orderItem = orderItem
    }

    OrderItem getOrderItem() {
        return orderItem
    }

    void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem
    }

    @Override
    Promise<OrderActionContext> execute(OrderActionContext request) {
        return Promise.wrap(SERVICE.submit(new Callable<OrderActionContext>() {
            OrderActionContext call() {
                request.orderServiceContext.order.orderItems << orderItem
                return request
            }
        } ))
    }
}
