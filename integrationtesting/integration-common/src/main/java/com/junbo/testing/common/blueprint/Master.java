package com.junbo.testing.common.blueprint;

import com.junbo.cart.spec.model.Cart;
import com.junbo.cart.spec.model.Offer;
import com.junbo.identity.spec.model.user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiefeng on 14-3-19.
 */
public class Master {

    private static Master instance = null;

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

    public void initializeMaster(){
        this.initializeUsers();
        this.initializeCarts();
        this.initializeOffers();
    }
    public void initializeUsers(){
        if (this.users == null){
            this.users = new HashMap<String, User>();
        }

        this.users.clear();
    }

    public void initializeCarts(){
        if (this.carts == null){
            this.carts = new HashMap<String, Cart>();
        }
        this.carts.clear();
    }

    public void initializeOffers(){
        if (this.offers == null){
            this.offers = new HashMap<String, Offer>();
        }
        this.offers.clear();
    }

    public void addUser(String userId, User user){
        if (this.users.containsKey(userId)){
            this.users.remove(userId);
        }
        this.users.put(user.getId().toString(), user);
    }

    public void addCart(String cartId, Cart cart){
        if (this.carts.containsKey(cartId)){
            this.carts.remove(cartId);
        }
        this.carts.put(cart.getId().toString(), cart);
    }

    public void addOffer(String offerId, Offer offer){
        if (this.offers.containsKey(offerId)){
            this.offers.remove(offerId);
        }

        this.offers.put(offer.getId().toString(),offer);
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
}
