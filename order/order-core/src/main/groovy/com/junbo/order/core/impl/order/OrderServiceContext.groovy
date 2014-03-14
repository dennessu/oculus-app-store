/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.identity.spec.model.user.User
import com.junbo.order.spec.model.Order
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/21/14.
 */
@Component('orderServiceContext')
@Scope('prototype')
@CompileStatic
class OrderServiceContext {

    Order order
    User user
    List<PaymentInstrument> paymentInstruments
    List<Balance> balances
    ShippingAddress shippingAddress
    List<Offer> offers
    List<Order> orders

    OrderServiceContext(Order o) {
        order = o
    }
}
