/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 2/21/14.
 */
@Component('orderServiceContextBuilder')
@CompileStatic
@TypeChecked
class OrderServiceContextBuilder {

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceContextBuilder)


    Promise<List<PaymentInstrument>> getPaymentInstruments(OrderServiceContext context) {

        assert (context != null && context.order != null)

        if (!CollectionUtils.isEmpty(context.paymentInstruments)) {
            return Promise.pure(context.paymentInstruments)
        }

        List<PaymentInfo> payments = context.order.payments

        if (payments == null || payments.isEmpty()) {
            return Promise.pure(null)
        }

        List<PaymentInstrument> pis = []
        return Promise.each(payments) { PaymentInfo paymentInfo ->
            return facadeContainer.paymentFacade.
                    getPaymentInstrument(paymentInfo.paymentInstrument.value).syncRecover { Throwable throwable ->
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

    Promise<List<Address>> getBillingAddresses(OrderServiceContext context) {
        assert (context != null && context.order != null)

        if (!CollectionUtils.isEmpty(context.billingAddresses)) {
            return Promise.pure(context.billingAddresses)
        }

        return getPaymentInstruments(context).then {
            List<Address> addresses =[]
            return Promise.each(context.paymentInstruments) { PaymentInstrument pi ->
                return facadeContainer.identityFacade.getAddress(pi.billingAddressId).syncThen { Address address ->
                    addresses << address
                }
            }.then {
                context.billingAddresses = addresses
                return Promise.pure(addresses)
            }
        }
    }

    Promise<List<Balance>> getBalances(OrderServiceContext context) {

        assert (context != null && context.order != null)

        if (!CollectionUtils.isEmpty(context.balances)) {
            return Promise.pure(context.balances)
        }
        return refreshBalances(context)
    }

    Promise<FulfilmentRequest> getFulfillmentRequest(OrderServiceContext context) {

        assert (context != null && context.order != null)

        if (!context.fulfillmentRequest) {
            return Promise.pure(context.fulfillmentRequest)
        }
        return refreshFulfillmentRequest(context)
    }

    Promise<List<Balance>> refreshBalances(OrderServiceContext context) {

        assert (context != null && context.order != null && context.order.getId() != null)

        return facadeContainer.billingFacade.getBalancesByOrderId(
                context.order.getId().value).syncThen { List<Balance> bas ->
            context.balances = bas
            return bas
        }
    }

    Promise<FulfilmentRequest> refreshFulfillmentRequest(OrderServiceContext context) {

        assert (context != null && context.order != null && context.order.getId() != null)

        return facadeContainer.fulfillmentFacade.getFulfillment(context.order.getId()).syncThen {FulfilmentRequest fr ->
            context.fulfillmentRequest = fr
            return fr
        }
    }

    Promise<Address> getShippingAddress(OrderServiceContext context) {
        assert (context != null && context.order != null)

        if (context.order.shippingAddress == null) {
            return Promise.pure(null)
        }

        if (context.shippingAddress != null) {
            return Promise.pure(context.shippingAddress)
        }
        return refreshShippingAddress(context)
    }

    Promise<Address> refreshShippingAddress(OrderServiceContext context) {

        assert (context != null && context.order != null)

        if (context.order.shippingAddress == null) {
            return Promise.pure(null)
        }

        return facadeContainer.identityFacade.getAddress(context.order.shippingAddress.value).syncThen {
            Address address ->
            context.shippingAddress = address
            return address
        }
    }


    Promise<User> getUser(OrderServiceContext context) {

        assert (context != null && context.order != null)

        if (context.order.user == null) {
            return Promise.pure(null)
        }

        if (context.user != null) {
            return Promise.pure(context.user)
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

        assert (context != null && context.order != null)

        if (CollectionUtils.isEmpty(context.order.orderItems) == null) {
            return Promise.pure(null)
        }

        if (context.offers != null) {
            return Promise.pure(context.offers)
        }

        List<OrderOfferRevision> offers = []
        return Promise.each(context.order.orderItems) { OrderItem oi ->
            return facadeContainer.catalogFacade.getOfferRevision(oi.offer.value).syncThen { OrderOfferRevision of ->
                offers << of
            }
        }.syncThen {
            def offerMap = new HashMap<OfferId, OrderOfferRevision>()
            offers?.each { OrderOfferRevision offer ->
                offerMap.put(new OfferId(offer.catalogOfferRevision.offerId), offer)
            }
            context.offersMap = offerMap
            context.offers = offers
            return offers
        }
    }

    Promise<Balance> getOrderEventBalance(OrderServiceContext context) {
        assert (context != null && context.order != null)

        if (context?.orderEvent?.billingInfo?.balance == null) {
            return Promise.pure(null)
        }

        return getBalances(context).then { List<Balance> balanceList ->
            return Promise.pure(balanceList.find { Balance balance ->
                return balance.getId() == context.orderEvent.billingInfo.balance
            })
        }
    }

    Promise<OrderOfferRevision> getOffer(OfferId offerId, OrderServiceContext context) {
        return getOffers(context).then { List<OrderOfferRevision> offers ->
            if (CollectionUtils.isEmpty(offers)) {
                return Promise.pure(null)
            }
            def offer = offers.find { OrderOfferRevision of ->
                of.catalogOfferRevision.offerId == offerId.value
            }
            return Promise.pure(offer)
        }
    }

    Promise<com.junbo.identity.spec.v1.model.Currency> getCurrency(OrderServiceContext context) {
        assert (context != null && context.order != null)

        if (context.order.currency == null) {
            return Promise.pure(null)
        }

        if (context.currency != null) {
            return Promise.pure(context.currency)
        }

        return facadeContainer.identityFacade.getCurrency(context.order.currency.value).then {
            com.junbo.identity.spec.v1.model.Currency currency ->
                assert (currency != null)
                context.currency = currency
                return Promise.pure(currency)
        }
    }

    Promise<String> getEmail(OrderServiceContext context)  {
        assert (context != null && context.order != null)

        if (context.email != null) {
            return Promise.pure(context.email)
        }

        return getUser(context).then { User user ->

            UserPersonalInfoId emailId = null
            for (UserPersonalInfoLink link : user.emails) {
                if (link.isDefault) {
                    emailId = link.value
                }
            }
            return facadeContainer.identityFacade.getEmail(emailId).recover { Throwable throwable ->
                LOGGER.error("name=Order_GetEmail_Error", throwable)
                return Promise.pure(null)
            }.then { String email ->
                context.email = email
                return Promise.pure(email)
            }
        }
    }
}
