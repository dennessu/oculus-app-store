/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
/**
 * Created by chriszhu on 2/21/14.
 */
@Component('orderServiceContextBuilder')
@CompileStatic
@TypeChecked
class OrderServiceContextBuilder {

    @Autowired
    OrderRepository orderRepository
    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceContextBuilder)


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
            facadeContainer.paymentFacade.
                    getPaymentInstrument(piid.value).syncRecover { Throwable throwable ->
                LOGGER.error('name=Order_GetPaymentInstrument_Error', throwable)
                // TODO read the payment error
                throw AppErrors.INSTANCE.paymentConnectionError().exception()
            }.syncThen { PaymentInstrument pi ->
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
        return facadeContainer.billingFacade.getBalancesByOrderId(
                context.order.id.value).syncThen { List<Balance> bas ->
            context.balances = bas
            return bas
        }
    }

    Promise<ShippingAddress> getShippingAddress(OrderServiceContext context) {
        if (context == null || context.order == null || context.order.shippingAddress == null) {
            return Promise.pure(null)
        }

        if (context.shippingAddress != null) {
            return Promise.pure(context.shippingAddress)
        }
        return refreshShippingAddress(context)
    }

    Promise<ShippingAddress> refreshShippingAddress(OrderServiceContext context) {

        if (context == null || context.order == null || context.order.shippingAddress == null) {
            return Promise.pure(null)
        }
        return facadeContainer.billingFacade.getShippingAddress(
                context.order.user.value, context.order.shippingAddress.value).syncThen { ShippingAddress sa ->
            context.shippingAddress = sa
            return sa
        }
    }


    Promise<User> getUser(OrderServiceContext context) {

        if (context == null || context.order == null || context.order.user == null) {
            return Promise.pure(null)
        }
        return facadeContainer.identityFacade.getUser(context.order.user.value).syncRecover { Throwable throwable ->
            LOGGER.error('name=User_Not_Found', throwable)
            throw AppErrors.INSTANCE.userNotFound(context.order.user.value.toString()).exception()
        }.syncThen { User user ->
            context.user = user
            return user
        }
    }

    Promise<List<OrderOfferRevision>> getOffers(OrderServiceContext context) {

        if (context == null || context.order == null || CollectionUtils.isEmpty(context.order.orderItems)) {
            return Promise.pure(Collections.emptyList())
        }

        List<OrderOfferRevision> offers = []
        return Promise.each(context.order.orderItems.iterator()) { OrderItem oi ->
            facadeContainer.catalogFacade.getOfferRevision(oi.offer.value).syncThen { OrderOfferRevision of ->
                offers << of
            }
        }.syncThen {
            def offerMap = new HashMap<OfferId, OrderOfferRevision>()
            offers?.each { OrderOfferRevision offer ->
                offerMap.put(new OfferId(offer.catalogOfferRevision.offerId), offer)
            }
            context.offers = offerMap
            return offers
        }
    }
}
