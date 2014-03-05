package com.junbo.order.core.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderflow.BaseSettleFlow
import com.junbo.order.core.mock.MockAction
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import org.testng.annotations.Test

/**
 * Created by fzhang on 14-2-26.
 */
class BaseSettleFlowTest {

    @Test
    void testFlowExecute() {
        for (int i = 1; i < 200; ++i) {
            flowExecute(i)
        }
    }

    private void flowExecute(int actionSize) {
        def context = new OrderServiceContext()
        List<OrderItem> orderItems = []
        for (int i = 0; i < actionSize; ++i) {
            orderItems << new OrderItem()
        }
        context.order = new Order()
        context.order.orderItems = []
        mockFlow(orderItems).execute(context).wrapped().get()
        assert context.order.orderItems.size() == orderItems.size()
        for (int i = 0; i < orderItems.size(); ++i) {
            assert context.order.orderItems[i].is(orderItems[i])
        }
    }

    private BaseSettleFlow mockFlow(List<OrderItem> orderItems) {
        List<MockAction> actions = []
        orderItems.each {
            actions << new MockAction(it)
        }
        return new BaseSettleFlow() {
            @Override
            UUID getName() {
                return UUID.randomUUID()
            }
            @Override
            Promise<List<Order>> execute(OrderServiceContext order) {
                return execute(order, actions)
            }
        }
    }
}
