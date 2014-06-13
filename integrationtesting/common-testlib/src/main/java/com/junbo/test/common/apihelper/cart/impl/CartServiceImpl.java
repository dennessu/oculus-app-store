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
import com.junbo.test.common.apihelper.cart.CartService;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by jiefeng on 14-3-19.
 * Caller helper for cart APIs
 */
public class CartServiceImpl implements CartService {

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

        byte[] bytes = new JsonMessageTranscoder().encode(cart);

        String requestBody = new String(bytes);

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts";

        Request req = new RequestBuilder("POST")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        },
                nettyResponse.getResponseBody());
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }

    public String getCart(String userId, String cartId) throws Exception {
        return getCart(userId, cartId, 200);
    }

    public String getCart(String userId, String cartId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId;

        Request req = new RequestBuilder("GET")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        },
                nettyResponse.getResponseBody());
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }

    public String getCartPrimary(String userId) throws Exception {
        return getCartPrimary(userId, 302);
    }

    public String getCartPrimary(String userId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/primary";

        Request req = new RequestBuilder("GET")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        if (nettyResponse.getStatusCode() == 302) {
            String redirectUrl = nettyResponse.getHeaders().get("Location").get(0);
            req = new RequestBuilder("GET")
                    .setUrl(redirectUrl)
                    .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                    .build();
            future = asyncClient.prepareRequest(req).execute();
            NettyResponse redirectResponse = (NettyResponse) future.get();
            logger.LogResponse(redirectResponse);
            Assert.assertEquals(200, redirectResponse.getStatusCode());
            Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, redirectResponse.getResponseBody());

            String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
            Master.getInstance().addCart(rtnCartId, rtnCart);
            return rtnCartId;
        }
        return null;
    }

    public List<String> getCartByName(String userId, String cartName) throws Exception {
        return getCartByName(userId, cartName, 200);
    }

    public List<String> getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts?cartName=" + cartName;

        Request req = new RequestBuilder("GET")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Results<Cart> cartGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Cart>>() {
        },
                nettyResponse.getResponseBody());
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
        byte[] bytes = new JsonMessageTranscoder().encode(cart);
        String requestBody = new String(bytes);

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId;

        Request req = new RequestBuilder("PUT")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        },
                nettyResponse.getResponseBody());
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }

    public String mergeCart(String userId, String cartId, Cart fromCart) throws Exception {
        return mergeCart(userId, cartId, fromCart, 200);
    }

    public String mergeCart(String userId, String cartId, Cart fromCart, int expectedResponseCode) throws Exception {
        byte[] bytes = new JsonMessageTranscoder().encode(fromCart);
        String requestBody = new String(bytes);

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts/" + cartId + "/merge";

        Request req = new RequestBuilder("POST")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .setBody(requestBody)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
        },
                nettyResponse.getResponseBody());
        String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
        Master.getInstance().addCart(rtnCartId, rtnCart);
        return rtnCartId;
    }
}
