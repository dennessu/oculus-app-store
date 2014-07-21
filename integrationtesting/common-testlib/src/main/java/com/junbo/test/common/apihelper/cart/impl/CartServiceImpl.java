/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.cart.impl;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.cart.CartService;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiefeng on 14-3-19.
 * Caller helper for cart APIs
 */
public class CartServiceImpl extends HttpClientBase implements CartService {

    private static String cartUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");

    private LogHelper logger = new LogHelper(CartServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static CartService instance;

    public static synchronized CartService getInstance() {
        if (instance == null) {
            instance = new CartServiceImpl();
        }
        return instance;
    }

    private CartServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String addCart(String userId, Cart cart) throws Exception {
        return addCart(userId, cart, 200);
    }

    public String addCart(String userId, Cart cart, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts";
        String responseBody = restApiCall(HTTPMethod.POST, cartEndpointUrl, cart, expectedResponseCode);

        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        }, responseBody);
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;

    }

    public String getCart(String userId, String cartId) throws Exception {
        return getCart(userId, cartId, 200);
    }

    public String getCart(String userId, String cartId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId;

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        }, responseBody);
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }

    public String getCartPrimary(String userId) throws Exception {
        return getCartPrimary(userId, 302);
    }

    public String getCartPrimary(String userId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/primary";

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);

        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        }, responseBody);

        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;

    }

    public List<String> getCartByName(String userId, String cartName) throws Exception {
        return getCartByName(userId, cartName, 200);
    }

    public List<String> getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts?cartName=" + cartName;

        String responseBody = restApiCall(HTTPMethod.GET, cartEndpointUrl, expectedResponseCode);

        Results<Cart> cartGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Cart>>() {
        }, responseBody);
        List<String> listCartId = new ArrayList<>();
        for (Cart cart : cartGet.getItems()) {
            String cartRtnId = IdConverter.idToHexString(cart.getId());
            Master.getInstance().addCart(cartRtnId, cart);
            listCartId.add(cartRtnId);
        }

        return listCartId;
    }

    public String updateCart(String userId, String cartId, Cart cart) throws Exception {
        return updateCart(userId, cartId, cart, 200);
    }

    public String updateCart(String userId, String cartId, Cart cart, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId;

        String responseBody = restApiCall(HTTPMethod.PUT, cartEndpointUrl, cart, expectedResponseCode);

        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        }, responseBody);
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }

    public String mergeCart(String userId, String cartId, Cart fromCart) throws Exception {
        return mergeCart(userId, cartId, fromCart, 200);
    }

    public String mergeCart(String userId, String cartId, Cart fromCart, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId + "/merge";

        String responseBody = restApiCall(HTTPMethod.POST, cartEndpointUrl, fromCart, expectedResponseCode);
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        }, responseBody);
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }
}
