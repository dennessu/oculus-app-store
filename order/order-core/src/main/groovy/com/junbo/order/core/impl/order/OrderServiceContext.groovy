/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
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
    Promise<List<PaymentInstrument>> paymentInstruments
    Promise<List<Balance>> balances
    Promise<ShippingAddress> shippingAddress

    @Autowired
    OrderRepository orderRepository
    @Autowired
    PaymentFacade paymentFacade
    @Autowired
    BillingFacade billingFacade
    @Autowired
    RatingFacade ratingFacade
    @Autowired
    IdentityFacade identityFacade
    @Autowired
    FulfillmentFacade fulfillmentFacade

    Long orderId

    Promise<List<PaymentInstrument>> getPaymentInstruments() {

        if (order == null || order.paymentInstruments == null || order.paymentInstruments.isEmpty()) {
            return null
        }
        // Lazy load
        List<PaymentInstrument> pis
        if (paymentInstruments == null) {
            pis = []
            paymentInstruments = Promise.each(order.paymentInstruments?.iterator()) {
                PaymentInstrumentId pmId ->
                paymentFacade.getPaymentInstrument(pmId.value).syncThen { PaymentInstrument pi ->
                    pis << pi
                }
            }.then {
                Promise.pure(pis)
            }
        }
        return paymentInstruments
    }

    Promise<List<Balance>> getBalances() {
        // Lazy load
        if (balances == null) {
            balances = refreshBalances()
        }
        return balances
    }

    Promise<List<Balance>> refreshBalances() {
        if (order == null || order.id == null) {
            return null
        }
        balances = billingFacade.getBalancesByOrderId(order.id.value)
        return balances
    }

    Promise<ShippingAddress> getShippingAddress() {
        if (order == null || order.shippingAddressId == null) {
            return null
        }
        // Lazy load
        if (shippingAddress == null) {
            shippingAddress = billingFacade.getShippingAddress(order.shippingAddressId.value)
        }
        return shippingAddress
    }
}
