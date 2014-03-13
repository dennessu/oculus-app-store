/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.cache.CachedCatalogFacadeImpl
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.clientproxy.payment.PaymentFacade
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
/**
 * Created by chriszhu on 2/21/14.
 */
@Component('orderServiceContextBuilder')
@CompileStatic
class OrderServiceContextBuilder {

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
    @Autowired
    CachedCatalogFacadeImpl cachedCatalogFacade

    Promise<List<PaymentInstrument>> getPaymentInstruments(OrderServiceContext context) {

        if (context == null || context.order == null) { return Promise.pure(null) }

        if (!CollectionUtils.isEmpty(context.paymentInstruments)) {
            return Promise.pure(context.paymentInstruments)
        }

        List<PaymentInstrumentId> piids = context.order.paymentInstruments

        if (piids == null || piids.isEmpty()) {
            return Promise.pure(null)
        }

        List<PaymentInstrument> pis = []
        return Promise.each(piids.iterator()) { PaymentInstrumentId piid ->
            paymentFacade.getPaymentInstrument(piid.value).syncThen { PaymentInstrument pi ->
                pis << pi
            }
        }.syncThen {
            context.paymentInstruments = pis
            return pis
        }
    }

    Promise<List<Balance>> getBalances(OrderServiceContext context) {

        if (context == null || context.order == null) { return null }

        if (!CollectionUtils.isEmpty(context.balances)) {
            return Promise.pure(context.balances)
        }
        return refreshBalances(context)
    }

    Promise<List<Balance>> refreshBalances(OrderServiceContext context) {

        if (context == null || context.order == null || context.order.id == null) {
            return Promise.pure(null)
        }
        return billingFacade.getBalancesByOrderId(context.order.id.value).syncThen { List<Balance> bas ->
            context.balances = bas
            return bas
        }
    }

    Promise<ShippingAddress> getShippingAddress(OrderServiceContext context) {
        if (context == null || context.order == null || context.order.shippingAddressId == null) {
            return Promise.pure(null)
        }

        if (context.shippingAddress != null) {
            return Promise.pure(context.shippingAddress)
        }
        return refreshShippingAddress(context)
    }

    Promise<ShippingAddress> refreshShippingAddress(OrderServiceContext context) {

        if (context == null || context.order == null || context.order.shippingAddressId == null) {
            return Promise.pure(null)
        }
        return billingFacade.getShippingAddress(context.order.user.value, context.order.shippingAddressId.value).
                syncThen { ShippingAddress sa ->
            context.shippingAddress = sa
            return sa
        }
    }


    Promise<User> getUser(OrderServiceContext context) {

        if (context == null || context.order == null || context.order.user == null) {
            return Promise.pure(null)
        }
        return identityFacade.getUser(context.order.user.value).syncThen { User user ->
            context.user = user
            return user
        }
    }

    Promise<List<Offer>> getOffers(OrderServiceContext context) {

        if (context == null || context.order == null || !CollectionUtils.isEmpty(context.order.orderItems)) {
            return Promise.pure(null)
        }

        List<Offer> offers
        // TODO timestamp
        return Promise.each(context.order.orderItems.iterator()) { OrderItem oi ->
            cachedCatalogFacade.getOffer(oi.offer.value).syncThen { Offer of ->
                offers << of
            }
        }.then {
            context.offers = offers
            return offers
        }
    }
}
