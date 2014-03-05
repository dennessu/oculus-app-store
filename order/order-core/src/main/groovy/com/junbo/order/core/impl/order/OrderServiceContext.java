/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.order;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.order.clientproxy.billing.BillingFacade;
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade;
import com.junbo.order.clientproxy.identity.IdentityFacade;
import com.junbo.order.clientproxy.payment.PaymentFacade;
import com.junbo.order.clientproxy.rating.RatingFacade;
import com.junbo.order.db.repo.OrderRepository;
import com.junbo.order.spec.model.Order;
import com.junbo.payment.spec.model.PaymentInstrument;

import java.util.List;

/**
 * Created by chriszhu on 2/21/14.
 */
public class OrderServiceContext {

    private Order order;
    private List<PaymentInstrument> paymentInstruments;
    private List<Balance> balances;
    private ShippingAddress shippingAddress;


    private OrderRepository orderRepository;
    private PaymentFacade paymentFacade;
    private BillingFacade billingFacade;
    private RatingFacade ratingFacade;
    private IdentityFacade identityFacade;
    private FulfillmentFacade fulfillmentFacade;

    private Long orderId;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<PaymentInstrument> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(List<PaymentInstrument> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PaymentFacade getPaymentFacade() {
        return paymentFacade;
    }

    public void setPaymentFacade(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    public BillingFacade getBillingFacade() {
        return billingFacade;
    }

    public void setBillingFacade(BillingFacade billingFacade) {
        this.billingFacade = billingFacade;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public RatingFacade getRatingFacade() {
        return ratingFacade;
    }

    public void setRatingFacade(RatingFacade ratingFacade) {
        this.ratingFacade = ratingFacade;
    }

    public IdentityFacade getIdentityFacade() {
        return identityFacade;
    }

    public void setIdentityFacade(IdentityFacade identityFacade) {
        this.identityFacade = identityFacade;
    }

    public FulfillmentFacade getFulfillmentFacade() {
        return fulfillmentFacade;
    }

    public void setFulfillmentFacade(FulfillmentFacade fulfillmentFacade) {
        this.fulfillmentFacade = fulfillmentFacade;
    }
}
