package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.CreateOrderAction
import com.junbo.order.core.impl.orderaction.FulfillmentAction
import com.junbo.order.core.impl.orderaction.ImmediateSettleAction
import com.junbo.order.core.impl.orderaction.RatingAction
import com.junbo.order.core.impl.orderaction.ValidateAction
import com.junbo.order.core.impl.orderaction.context.BaseContext
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class ImmediateSettleFlow extends BaseSettleFlow {
    @Override
    UUID getName() {
        return UUID.fromString('F54E7406-8801-4D99-B82E-E297C597DB4F')
    }

    @Override
    Promise<List<Order>> execute(OrderServiceContext order) {
        BaseContext context = new BaseContext()
        context.setOrderServiceContext(order)
        return execute(order, (List<OrderAction>) [
                new ValidateAction(),
                new RatingAction(),
                new CreateOrderAction(),
                new ImmediateSettleAction(),
                new FulfillmentAction()
        ] )
    }
}
