/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.blueprint;

import com.junbo.billing.spec.model.Balance;
import com.junbo.cart.spec.model.Cart;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.order.spec.model.Order;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.RandomFactory;

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
    private Map<String, Item> items;
    private Map<String, Offer> offers;
    private Map<String, ItemRevision> itemRevisions;
    private Map<String, OfferRevision> offerRevisions;
    private Map<String, Order> orders;
    private Map<String, ItemAttribute> itemAttributes;
    private Map<String, OfferAttribute> offerAttributes;
    private Map<String, Organization> organizations;
    private final String defaultLocale = "en_US";

    private Map<String, PaymentInstrument> paymentInstruments;
    private Map<String, Entitlement> entitlements;
    private Map<String, Balance> balances;
    private Map<String, FulfilmentRequest> fulfilments;

    private Map<String, String> userAccessTokens;
    private Map<ComponentType, String> serviceAccessTokens;

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    private String userPassword;

    private String currentUid;

    private String apiErrorMsg;

    private String primaryCommerceEndPointUrl;
    private String secondaryCommerceEndPointUrl;

    private EndPointType endPointType;

    public void initializeMaster() {
        this.initializeUsers();
        this.initializeCarts();
        this.initializeItems();
        this.initializeItemRevisions();
        this.initializeOffers();
        this.initializeOfferRevisions();
        this.initializeOrders();
        this.initializeItemAttributes();
        this.initializeOfferAttributes();
        this.initializeEntitlements();
        this.initializePayments();
        this.initializeBalances();
        this.initializeFulfilmentItems();
        this.initializeUserAccessTokens();
        this.initializeServiceAccessTokens();
        this.initializeOrgnizations();
        this.currentUid = new String();
        this.endPointType = EndPointType.Primary;
    }

    /**
     * Enum for Component name.
     *
     * @author Yunlongzhao
     */
    public enum EndPointType {
        Primary,
        Secondary,
        Random
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

    public void initializeItems() {
        if (this.items == null) {
            this.items = new HashMap<>();
        }
        this.items.clear();
    }

    public void initializeItemRevisions() {
        if (this.itemRevisions == null) {
            this.itemRevisions = new HashMap<>();
        }
        this.itemRevisions.clear();
    }

    public void initializeOffers() {
        if (this.offers == null) {
            this.offers = new HashMap<>();
        }
        this.offers.clear();
    }

    public void initializeOfferRevisions() {
        if (this.offerRevisions == null) {
            this.offerRevisions = new HashMap<>();
        }
        this.offerRevisions.clear();
    }

    public void initializeOrders() {
        if (this.orders == null) {
            this.orders = new HashMap<>();
        }
        this.orders.clear();
    }

    public void initializeItemAttributes() {
        if (this.itemAttributes == null) {
            this.itemAttributes = new HashMap<>();
        }
        this.itemAttributes.clear();
    }

    public void initializeOfferAttributes() {
        if (this.offerAttributes == null) {
            this.offerAttributes = new HashMap<>();
        }
        this.offerAttributes.clear();
    }

    public void initializeEntitlements() {
        if (this.entitlements == null) {
            this.entitlements = new HashMap<>();
        }
        this.entitlements.clear();
    }

    public void initializePayments() {
        if (this.paymentInstruments == null) {
            this.paymentInstruments = new HashMap<>();
        }
        this.paymentInstruments.clear();
    }

    public void initializeBalances() {
        if (this.balances == null) {
            this.balances = new HashMap<>();
        }
        this.balances.clear();
    }

    public void initializeUserAccessTokens() {
        if (this.userAccessTokens == null) {
            this.userAccessTokens = new HashMap<>();
        }
        this.userAccessTokens.clear();
    }

    public void initializeServiceAccessTokens() {
        if (this.serviceAccessTokens == null) {
            this.serviceAccessTokens = new HashMap<>();
        }
        this.serviceAccessTokens.clear();
    }

    public void initializeOrgnizations() {
        if (this.organizations == null) {
            this.organizations = new HashMap<>();
        }
        this.organizations.clear();
    }

    public void initializeFulfilmentItems() {
        if (this.fulfilments == null) {
            this.fulfilments = new HashMap<>();
        }
        this.fulfilments.clear();
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

    public void addItem(String itemId, Item item) {
        if (this.items.containsKey(itemId)) {
            this.items.remove(itemId);
        }
        this.items.put(itemId, item);
    }

    public void addItemRevision(String itemRevisionId, ItemRevision itemRevision) {
        if (this.itemRevisions.containsKey(itemRevisionId)) {
            this.itemRevisions.remove(itemRevisionId);
        }
        this.itemRevisions.put(itemRevisionId, itemRevision);
    }

    public void addOffer(String offerId, Offer offer) {
        if (this.offers.containsKey(offerId)) {
            this.offers.remove(offerId);
        }
        this.offers.put(offerId, offer);
    }

    public void addOfferRevision(String offerRevisionId, OfferRevision offerRevision) {
        if (this.offerRevisions.containsKey(offerRevisionId)) {
            this.offerRevisions.remove(offerRevisionId);
        }
        this.offerRevisions.put(offerRevisionId, offerRevision);
    }

    public void addOrder(String orderId, Order order) {
        if (this.orders.containsKey(orderId)) {
            this.orders.remove(orderId);
        }

        this.orders.put(orderId, order);
    }

    public void addItemAttribute(String attributeId, ItemAttribute attribute) {
        if (this.itemAttributes.containsKey(attributeId)) {
            this.itemAttributes.remove(attributeId);
        }
        this.itemAttributes.put(attributeId, attribute);
    }

    public void addOfferAttribute(String attributeId, OfferAttribute attribute) {
        if (this.offerAttributes.containsKey(attributeId)) {
            this.offerAttributes.remove(attributeId);
        }
        this.offerAttributes.put(attributeId, attribute);
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

    public void addBalances(String balanceId, Balance balance) {
        if (this.balances.containsKey(balanceId)) {
            this.balances.remove(balanceId);
        }
        this.balances.put(balanceId, balance);
    }

    public void addFulfilment(String fulfilmentId, FulfilmentRequest fulfilmentRequest) {
        if (this.fulfilments.containsKey(fulfilmentId)) {
            this.fulfilments.remove(fulfilmentId);
        }
        this.fulfilments.put(fulfilmentId, fulfilmentRequest);
    }

    public void addUserAccessToken(String uid, String accessToken) {
        if (this.userAccessTokens.containsKey(uid)) {
            this.userAccessTokens.remove(uid);
        }
        this.userAccessTokens.put(uid, accessToken);
    }

    public void addServiceAccessToken(ComponentType componentType, String accessToken) {
        if (this.serviceAccessTokens.containsKey(componentType)) {
            this.serviceAccessTokens.remove(componentType);
        }
        this.serviceAccessTokens.put(componentType, accessToken);
    }

    public void addOrganization(String organizationId, Organization organization) {
        this.organizations.put(organizationId, organization);
    }

    public User getUser(String userId) {
        return this.users.get(userId);
    }

    public Item getItem(String itemId) {
        return this.items.get(itemId);
    }

    public ItemRevision getItemRevision(String itemRevisionId) {
        return this.itemRevisions.get(itemRevisionId);
    }

    public Offer getOffer(String offerId) {
        return this.offers.get(offerId);
    }

    public OfferRevision getOfferRevision(String offerRevisionId) {
        return this.offerRevisions.get(offerRevisionId);
    }

    public Cart getCart(String cartId) {
        return this.carts.get(cartId);
    }

    public Order getOrder(String orderId) {
        return this.orders.get(orderId);
    }

    public Organization getOrganization(String organizationId) {
        return this.organizations.get(organizationId);
    }

    public PaymentInstrument getPaymentInstrument(String paymentInstrumentId) {
        return this.paymentInstruments.get(paymentInstrumentId);
    }

    public ItemAttribute getItemAttribute(String attributeId) {
        return this.itemAttributes.get(attributeId);
    }

    public OfferAttribute getOfferAttribute(String attributeId) {
        return this.offerAttributes.get(attributeId);
    }

    public Entitlement getEntitlement(String entitlementId) {
        return this.entitlements.get(entitlementId);
    }

    public Balance getBalance(String balanceId) {
        return this.balances.get(balanceId);
    }

    public FulfilmentRequest getFulfilment(String fulfilmentId) {
        return this.fulfilments.get(fulfilmentId);
    }

    public String getUserAccessToken(String uid) {
        return this.userAccessTokens.get(uid);
    }

    public String getServiceAccessToken(ComponentType componentType) {
        return this.serviceAccessTokens.get(componentType);
    }

    public Map<String, PaymentInstrument> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void removePaymentInstrument(String paymentInstrumentId) {
        if (this.paymentInstruments.containsKey(paymentInstrumentId)) {
            this.paymentInstruments.remove(paymentInstrumentId);
        }
    }

    public void removeBalance(String balanceId) {
        if (this.balances.containsKey(balanceId)) {
            this.balances.remove(balanceId);
        }
    }

    public void removeItem(String itemId) {
        if (this.items.containsKey(itemId)) {
            this.items.remove(itemId);
        }
    }

    public void removeOffer(String offerId) {
        if (this.offers.containsKey(offerId)) {
            this.offers.remove(offerId);
        }
    }

    public void removeItemAttribute(String itemAttributeId) {
        if (this.itemAttributes.containsKey(itemAttributeId)) {
            this.itemAttributes.remove(itemAttributeId);
        }
    }

    public void removeOfferAttribute(String offerAttributeId) {
        if (this.offerAttributes.containsKey(offerAttributeId)) {
            this.offerAttributes.remove(offerAttributeId);
        }
    }

    public void removeItemRevision(String itemRevisionId) {
        if (this.itemRevisions.containsKey(itemRevisionId)) {
            this.itemRevisions.remove(itemRevisionId);
        }
    }

    public void removeOfferRevision(String offerRevisionId) {
        if (this.offerRevisions.containsKey(offerRevisionId)) {
            this.offerRevisions.remove(offerRevisionId);
        }
    }

    public String getCurrentUid() {
        return currentUid;
    }

    public void setCurrentUid(String currentUid) {
        this.currentUid = currentUid;
    }

    public String getOfferIdByName(String offerName) {

        for (Map.Entry<String, Offer> entry : offers.entrySet()) {
            String key = entry.getKey();
            Offer offer = entry.getValue();
            if (offer.getCurrentRevisionId() != null) {
                OfferRevision offerRevision = this.offerRevisions.get(offer.getCurrentRevisionId());

                try {
                    if (offerRevision != null && offerRevision.getLocales().get(defaultLocale).getName().equalsIgnoreCase(offerName)) {
                        return key;
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getItemIdByName(String itemName) {

        for (Map.Entry<String, Item> entry : items.entrySet()) {
            String key = entry.getKey();
            Item item = entry.getValue();
            if (item.getCurrentRevisionId() != null) {
                ItemRevision itemRevision = this.itemRevisions.get(item.getCurrentRevisionId());

                try {
                    if (itemRevision != null && itemRevision.getLocales().get(defaultLocale).getName().equalsIgnoreCase(itemName)) {
                        return key;
                    }
                } catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getApiErrorMsg() {
        return apiErrorMsg;
    }

    public void setApiErrorMsg(String apiErrorMsg) {
        this.apiErrorMsg = apiErrorMsg;
    }

    public String getPrimaryCommerceEndPointUrl() {
        return primaryCommerceEndPointUrl == null ? ConfigHelper.getSetting("defaultCommerceEndpoint") : primaryCommerceEndPointUrl;
    }

    public void setPrimaryCommerceEndPointUrl(String primaryCommerceEndPointUrl) {
        this.primaryCommerceEndPointUrl = primaryCommerceEndPointUrl;
    }

    public String getSecondaryCommerceEndPointUrl() {
        return secondaryCommerceEndPointUrl;
    }

    public void setSecondaryCommerceEndPointUrl(String secondaryCommerceEndPointUrl) {
        this.secondaryCommerceEndPointUrl = secondaryCommerceEndPointUrl;
    }

    public EndPointType getEndPointType() {
        if (endPointType.equals(EndPointType.Random)) {
            switch (RandomFactory.getRandomInteger(0, 2)) {
                case 0:
                    return endPointType.Primary;
                case 1:
                    return endPointType.Secondary;
                default:
                    throw new TestException("No such endpoint type");
            }
        }
        return endPointType;
    }

    public EndPointType getCurrentEndPointType() {
        return endPointType;
    }

    public void setEndPointType(EndPointType endPointType) {
        this.endPointType = endPointType;
    }

}
