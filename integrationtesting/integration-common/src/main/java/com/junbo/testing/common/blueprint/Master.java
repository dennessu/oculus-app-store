/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.blueprint;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.cart.spec.model.Cart;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.identity.spec.model.user.User;
import com.junbo.order.spec.model.Order;
import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.payment.spec.model.PaymentInstrument;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiefeng on 14-3-19.
 */
public class Master {

    private static Master instance;

    public static synchronized Master getInstance() {
        if (instance == null) {
            instance = new Master();
        }
        return instance;
    }

    private Master() {
        this.initializeMaster();
    }

    private Map<String, User> users;
    private Map<String, Cart> carts;
    private Map<String, Offer> offers;
    private Map<String, Item> items;
    private Map<String, Order> orders;
    private Map<String, ShippingAddress> shippingAddresses;
    private Map<String, Attribute> attributes;
    private Map<String, PaymentInstrument> paymentInstruments;
    private Map<String, Entitlement> entitlements;

    public void initializeMaster() {
        this.initializeUsers();
        this.initializeCarts();
        this.initializeOffers();
        this.initializeItems();
        this.initializeOrders();
        this.initializeAttributes();
        this.initializeEntitlements();
        this.initializePayments();
        this.initializeShippingAddresses();
    }

    public void initializeUsers() {
        if (this.users == null) {
            this.users = new HashMap<>();
        }
        this.users.clear();
    }

    public void initializeCarts() {
        if (this.carts == null) {
            this.carts = new HashMap<>();
        }
        this.carts.clear();
    }

    public void initializeOffers() {
        if (this.offers == null) {
            this.offers = new HashMap<>();
        }
        this.offers.clear();
    }

    public void initializeItems() {
        if (this.items == null) {
            this.items = new HashMap<>();
        }
        this.items.clear();
    }

    public void initializeOrders() {
        if (this.orders == null) {
            this.orders = new HashMap<>();
        }
        this.orders.clear();
    }

    public void initializeAttributes() {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.clear();
    }

    public void initializeEntitlements() {
        if (this.entitlements == null) {
            this.entitlements = new HashMap<>();
        }
        this.entitlements.clear();
    }

    public void initializeShippingAddresses() {
        if (this.shippingAddresses == null) {
            this.shippingAddresses = new HashMap<>();
        }
        this.shippingAddresses.clear();
    }

    public void initializePayments() {
        if (this.paymentInstruments == null) {
            this.paymentInstruments = new HashMap<>();
        }
        this.paymentInstruments.clear();
    }

    public void addUser(String userId, User user) {
        if (this.users.containsKey(userId)) {
            this.users.remove(userId);
        }
        this.users.put(userId, user);
    }

    public void addCart(String cartId, Cart cart) {
        if (this.carts.containsKey(cartId)) {
            this.carts.remove(cartId);
        }
        this.carts.put(cartId, cart);
    }

    public void addOffer(String offerId, Offer offer) {
        if (this.offers.containsKey(offerId)) {
            this.offers.remove(offerId);
        }
        this.offers.put(offerId, offer);
    }

    public void addItem(String itemId, Item item) {
        if (this.items.containsKey(itemId)) {
            this.items.remove(itemId);
        }
        this.items.put(itemId, item);
    }

    public void addOrder(String orderId, Order order) {
        if (this.orders.containsKey(orderId)) {
            this.orders.remove(orderId);
        }

        this.orders.put(orderId, order);
    }

    public void addShippingAddress(String addressId, ShippingAddress address) {
        if (this.shippingAddresses.containsKey(addressId)) {
            this.shippingAddresses.remove(addressId);
        }

        this.shippingAddresses.put(addressId, address);
    }

    public void addAttribute(String attributeId, Attribute attribute) {
        if (this.attributes.containsKey(attributeId)) {
            this.attributes.remove(attributeId);
        }
        this.attributes.put(attributeId, attribute);
    }

    public void addPaymentInstrument(String paymentInstrumentId, PaymentInstrument paymentInstrument) {
        if (this.paymentInstruments.containsKey(paymentInstrumentId)) {
            this.paymentInstruments.remove(paymentInstrument);
        }

        this.paymentInstruments.put(paymentInstrumentId, paymentInstrument);
    }

    public void addEntitlement(String entitlementId, Entitlement entitlement) {
        if (this.entitlements.containsKey(entitlementId)) {
            this.entitlements.remove(entitlementId);
        }
        this.entitlements.put(entitlementId, entitlement);
    }

    public User getUser(String userId) {
        return this.users.get(userId);
    }

    public Offer getOffer(String offerId) {
        return this.offers.get(offerId);
    }

    public Cart getCart(String cartId) {
        return this.carts.get(cartId);
    }

    public Item getItem(String itemId) {
        return this.items.get(itemId);
    }

    public Order getOrder(String orderId) {
        return this.orders.get(orderId);
    }

    public ShippingAddress getShippingAddress(String addressId) {
        return this.shippingAddresses.get(addressId);
    }

    public PaymentInstrument getPaymentInstrument(String paymentInstrumentId) {
        return this.paymentInstruments.get(paymentInstrumentId);
    }

    public Attribute getAttribute(String attributeId) {
        return this.attributes.get(attributeId);
    }

    public Entitlement getEntitlement(String entitlementId) {
        return this.entitlements.get(entitlementId);
    }

    public void removeShippingAddress(String addressId) {
        if (this.shippingAddresses.containsKey(addressId)) {
            this.shippingAddresses.remove(addressId);
        }
    }

    public void removePaymentInstrument(String paymentInstrumentId) {
        if (this.paymentInstruments.containsKey(paymentInstrumentId)) {
            this.paymentInstruments.remove(paymentInstrumentId);
        }
    }

}
