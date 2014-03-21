/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.blueprint;

import com.junbo.cart.spec.model.Cart;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.identity.spec.model.user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiefeng on 14-3-19.
 */
public class Master {

    private static Master instance;

    public static synchronized Master getInstance(){
        if (instance == null) {
            instance = new Master();
        }
       return instance;
    }

    private Master(){
        this.initializeMaster();
    }

    private Map<String, User> users;
    private Map<String, Cart> carts;
    private Map<String, Offer> offers;
    private Map<String, Item> items;

    public void initializeMaster(){
        this.initializeUsers();
        this.initializeCarts();
        this.initializeOffers();
        this.initializeItems();
    }
    public void initializeUsers(){
        if (this.users == null){
            this.users = new HashMap<>();
        }

        this.users.clear();
    }

    public void initializeCarts(){
        if (this.carts == null){
            this.carts = new HashMap<>();
        }
        this.carts.clear();
    }

    public void initializeOffers(){
        if (this.offers == null){
            this.offers = new HashMap<>();
        }
        this.offers.clear();
    }

    public void initializeItems(){
        if (this.items == null){
            this.items = new HashMap<>();
        }
        this.items.clear();
    }

    public void addUser(String userId, User user){
        if (this.users.containsKey(userId)){
            this.users.remove(userId);
        }
        this.users.put(userId, user);
    }

    public void addCart(String cartId, Cart cart){
        if (this.carts.containsKey(cartId)){
            this.carts.remove(cartId);
        }
        this.carts.put(cartId, cart);
    }

    public void addOffer(String offerId, Offer offer){
        if (this.offers.containsKey(offerId)){
            this.offers.remove(offerId);
        }

        this.offers.put(offerId, offer);
    }

    public void addItem(String itemId, Item item){
        if (this.items.containsKey(itemId)){
            this.items.remove(itemId);
        }

        this.items.put(itemId, item);
    }

    public User getUser(String userId){
        return this.users.get(userId);
    }

    public Offer getOffer(String offerId){
        return this.offers.get(offerId);
    }

    public Cart getCart(String cartId){
        return this.carts.get(cartId);
    }

    public Item getItem(String itemId){
        return this.items.get(itemId);
    }
}
