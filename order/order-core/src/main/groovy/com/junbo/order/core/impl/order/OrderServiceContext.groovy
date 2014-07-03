/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.OfferId
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.model.ApiContext
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

    public OrderServiceContext() {
    }

    public OrderServiceContext(Order order, ApiContext apiContext) {
        this.order = order
        this.apiContext = apiContext
    }

    Order order
    User user
    String email
    ApiContext apiContext
    List<PaymentInstrument> paymentInstruments
    List<Address> billingAddresses
    List<Balance> balances
    Address shippingAddress
    Boolean isAsyncCharge
    String riskTransactionId
    Currency currency
    FulfilmentRequest fulfillmentRequest

    /**
     * Offers in order in map structure
     */
    Map<OfferId, OrderOfferRevision> offersMap
    /**
     * Offers in order in list structure with the same sequence of order items
     */
    List<OrderOfferRevision> offers
    OrderEvent orderEvent

    OrderServiceContext(Order o) {
        order = o
    }
}
