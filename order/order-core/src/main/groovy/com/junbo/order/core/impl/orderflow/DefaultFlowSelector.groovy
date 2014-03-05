package com.junbo.order.core.impl.orderflow

import com.junbo.order.core.FlowSelector
import com.junbo.order.core.OrderFlow
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.spec.model.ItemType
import com.junbo.order.spec.model.OrderType
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@Component('defaultFlowSelector')
class DefaultFlowSelector implements FlowSelector {

    @Override
    OrderFlow select(OrderServiceContext expOrder, OrderServiceOperation operation) {
        switch (operation) {
            case OrderServiceOperation.CREATE:
                return selectCreateOrderFlow(expOrder)
            case OrderServiceOperation.GET:
                return new GetOrderFlow()
            default:
                return null
        }
    }

    private static OrderFlow selectCreateOrderFlow(OrderServiceContext expOrder) {
        switch (expOrder.order?.type) {
            case OrderType.PAY_IN.toString():
                return selectPayInFlow(expOrder)
            default:
                return null
        }
        return null
    }

    private static OrderFlow selectPayInFlow(OrderServiceContext expOrder) {

        if (expOrder == null) {
            return null
        }

        // select order flow per payment info and product item info
        if (expOrder.paymentInstruments == null || expOrder.paymentInstruments.isEmpty()) {
            return new FreeSettleFlow()
        }

        // TODO: do not support multiple payment methods now
        switch (expOrder.paymentInstruments[0]?.type) {
            // TODO reference to payment instrument type
            case 'CREDIT_CARD':
                // TODO: do not support mixed order containing both physical item & digital item now
                switch (expOrder.order.orderItems[0]?.type) {
                    case ItemType.DIGITAL.toString():
                        return new ImmediateSettleFlow()
                    case ItemType.PHYSICAL.toString():
                        return new AuthSettleFlow()
                    default:
                        return null
                }
            default:
                return null
        }
    }
}
