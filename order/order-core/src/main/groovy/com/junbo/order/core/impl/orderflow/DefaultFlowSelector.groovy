/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.orderflow

import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.db.entity.enums.ItemType
import com.junbo.order.db.entity.enums.OrderType
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@Component('defaultFlowSelector')
class DefaultFlowSelector implements FlowSelector {

    @Override
    Promise<FlowType> select(OrderServiceContext expOrder, OrderServiceOperation operation) {
        switch (operation) {
            case OrderServiceOperation.CREATE:
                return selectCreateOrderFlow(expOrder)
            case OrderServiceOperation.GET:
                return Promise.pure(FlowType.GET_ORDER)
            default:
                return null
        }
    }

    private static Promise<FlowType> selectCreateOrderFlow(OrderServiceContext expOrder) {
        switch (expOrder.order?.type) {
            case OrderType.PAY_IN.toString():
                return selectPayInFlow(expOrder)
            default:
                return null
        }
        return null
    }

    private static Promise<FlowType> selectPayInFlow(OrderServiceContext context) {

        if (context == null) {
            return null
        }

        if (context.order?.paymentInstruments == null || context.order?.paymentInstruments.isEmpty()) {
            return Promise.pure(FlowType.FREE_SETTLE)
        }
        // select order flow per payment info and product item info
        context.paymentInstruments.syncThen { List<PaymentInstrument> pis ->
            // TODO: do not support multiple payment methods now
            switch (pis[0]?.type) {
                // TODO reference to payment instrument type
                case 'CREDIT_CARD':
                    // TODO: do not support mixed order containing both physical item & digital item now
                    switch (context.order.orderItems[0]?.type) {
                        case ItemType.DIGITAL.toString():
                            return FlowType.IMMEDIATE_SETTLE
                        case ItemType.PHYSICAL.toString():
                            return FlowType.AUTH_SETTLE
                        default:
                            return null
                    }
                default:
                    return null
            }
        }
    }
}