/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.order.spec.model.Order
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
/**
 * Created by chriszhu on 2/21/14.
 */

@CompileStatic
class OrderServiceContext {

    Order order
    List<PaymentInstrument> paymentInstruments
    List<Balance> balances
    ShippingAddress shippingAddress

    OrderServiceContext(Order o) {
        order = o
    }
}
