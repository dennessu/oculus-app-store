package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.OrderAction
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderaction.AuthSettleAction
import com.junbo.order.core.impl.orderaction.CreateOrderAction
import com.junbo.order.core.impl.orderaction.FulfillmentAction
import com.junbo.order.core.impl.orderaction.RatingAction
import com.junbo.order.core.impl.orderaction.ValidateAction
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class AuthSettleFlow extends BaseSettleFlow {
    @Override
    UUID getName() {
        return UUID.fromString('4701EA2D-960D-4237-8391-11A52CFFFE4B')
    }

    @Override
    Promise<List<Order>> execute(OrderServiceContext order) {
        OrderActionContext context = new OrderActionContext()
        context.setOrderServiceContext(order)

        return execute(order,  (List<OrderAction>) [
                new ValidateAction(),
                new RatingAction(),
                new CreateOrderAction(),
                new AuthSettleAction(),
                new FulfillmentAction()
        ] )
    }
}
