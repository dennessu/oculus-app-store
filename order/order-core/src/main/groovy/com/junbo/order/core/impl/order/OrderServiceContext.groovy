/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.OfferId
import com.junbo.identity.spec.v1.model.User
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderEvent
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/21/14.
 */
@Component('orderServiceContext')
@Scope('prototype')
@CompileStatic
@TypeChecked
class OrderServiceContext {

    Order order
    User user
    List<PaymentInstrument> paymentInstruments
    List<Balance> balances
    ShippingAddress shippingAddress
    Map<OfferId, OrderOfferRevision> offers
    OrderEvent orderEvent

    OrderServiceContext(Order o) {
        order = o
    }
}
