/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.cart;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by jiefeng on 14-4-1.
 * Caller helper for cart APIs
 */
public class CartService {

    private static String cartUrl = RestUrl.getRestUrl(RestUrl.ComponentName.COMMERCE);
    private static LogHelper logger = new LogHelper(Cart.class);
    private static AsyncHttpClient asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());

    private CartService() {
    }

    public static String addCart(String userId, Cart cart) throws Exception {
        return addCart(userId, cart, 200);
    }

    public static String addCart(String userId, Cart cart, int expectedResponseCode) throws Exception {

        String requestBody = new JsonMessageTranscoder().encode(cart);

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

    public static String getCart(String userId, String cartId) throws Exception {
        return getCart(userId, cartId, 200);
    }

    public static String getCart(String userId, String cartId, int expectedResponseCode) throws Exception {
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
        if (expectedResponseCode == 200) {
            Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            },
                    nettyResponse.getResponseBody());

            //Cart rtnCart = (Cart) HttpclientHelper.SimpleGet(cartEndpointUrl, Cart.class);
            String rtnCartId = IdConverter.idToHexString(rtnCart.getId());
            Master.getInstance().addCart(rtnCartId, rtnCart);
            return rtnCartId;
        }
        else{
            return null;
        }
    }

    public static String getCartPrimary(String userId) throws Exception {
        return getCartPrimary(userId, 302);
    }

    public static String getCartPrimary(String userId, int expectedResponseCode) throws Exception {
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

    public static List<String> getCartByName(String userId, String cartName) throws Exception {
        return getCartByName(userId, cartName, 200);
    }

    public static List<String> getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception {

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

        Results<Cart> cartGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Cart>>() {},
                nettyResponse.getResponseBody());
        List<String> listCartId = new ArrayList<>();
        for (Cart cart : cartGet.getItems()){
            String cartRtnId = IdConverter.idToHexString(cart.getId());
            Master.getInstance().addCart(cartRtnId, cart);
            listCartId.add(cartRtnId);
        }

        return listCartId;
    }

    public static String updateCart(String userId, String cartId, Cart cart) throws Exception {
        return updateCart(userId, cartId, cart, 200);
    }

    public static String updateCart(String userId, String cartId, Cart cart, int expectedResponseCode)
            throws Exception {
        String requestBody = new JsonMessageTranscoder().encode(cart);

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

    public static String mergeCart(String userId, String cartId, Cart fromCart) throws Exception {
        return mergeCart(userId, cartId, fromCart, 200);
    }

    public static String mergeCart(String userId, String cartId, Cart fromCart, int expectedResponseCode)
            throws Exception {
        String requestBody = new JsonMessageTranscoder().encode(fromCart);

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
