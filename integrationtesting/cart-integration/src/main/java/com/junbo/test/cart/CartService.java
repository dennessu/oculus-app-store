/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.cart;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.id.CartId;
import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;


/**
 * Created by jiefeng on 14-4-1.
 * Caller helper for cart APIs
 */
public class CartService extends HttpClientBase {

    private static CartService instance;

    private static String commerceUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");
    private static LogHelper logger = new LogHelper(Cart.class);
    private static AsyncHttpClient asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());

    private CartService() {
    }

    public static synchronized CartService getInstance() {
        if (instance == null) {
            instance = new CartService();
        }
        return instance;
    }

    public Cart addCart(UserId userId, Cart cart) throws Exception {
        return addCart(userId, cart, 200);
    }

    public Cart addCart(UserId userId, Cart cart, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts";

        String responseBody = restApiCall(HTTPMethod.POST, cartEndpointUrl, cart, expectedResponseCode);
        if (responseBody != null && !responseBody.isEmpty()) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, responseBody));
        }
        return null;
    }

    public Cart getCart(UserId userId, CartId cartId) throws Exception {
        return getCart(userId, cartId, 200);
    }

    public Cart getCart(UserId userId, CartId cartId, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = commerceUrl + "users/" +
                IdConverter.idToHexString(userId) + "/carts/" + IdConverter.idToHexString(cartId);

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);
        if (responseBody != null && !responseBody.isEmpty() && expectedResponseCode == 200) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, responseBody));
        }
        return null;
    }

    public Cart getCartPrimary(UserId userId) throws Exception {
        return getCartPrimary(userId, 302);
    }

    public Cart getCartPrimary(UserId userId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts/primary";

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);

        if (responseBody != null && !responseBody.isEmpty()) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, responseBody));
        }
        return null;
    }

    public Results<Cart> getCartByName(UserId userId, String cartName) throws Exception {
        return getCartByName(userId, cartName, 200);
    }

    public Results<Cart> getCartByName(UserId userId, String cartName, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts?cartName=" + cartName;

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);

        if (responseBody != null && !responseBody.isEmpty()) {
            Results<Cart> cartGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Cart>>() {
            }, responseBody);
            return cartGet;
        }
        return null;
    }

    public Cart updateCart(UserId userId, CartId cartId, Cart cart) throws Exception {
        return updateCart(userId, cartId, cart, 200);
    }

    public Cart updateCart(UserId userId, CartId cartId, Cart cart, int expectedResponseCode)
            throws Exception {
        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId)
                + "/carts/" + IdConverter.idToHexString(cartId);

        String responseBody = restApiCall(HTTPMethod.PUT, cartEndpointUrl, cart, expectedResponseCode);

        if (responseBody != null && !responseBody.isEmpty()) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, responseBody));
        }
        return null;
    }

    public Cart mergeCart(UserId userId, CartId cartId, Cart fromCart) throws Exception {
        return mergeCart(userId, cartId, fromCart, 200);
    }

    public Cart mergeCart(UserId userId, CartId cartId, Cart fromCart, int expectedResponseCode)
            throws Exception {
        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId)
                + "/carts/" + IdConverter.idToHexString(cartId) + "/merge";

        String responseBody = restApiCall(HTTPMethod.POST, cartEndpointUrl, fromCart, expectedResponseCode);

        if (responseBody != null && !responseBody.isEmpty()) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, responseBody));
        }
        return null;
    }
}
