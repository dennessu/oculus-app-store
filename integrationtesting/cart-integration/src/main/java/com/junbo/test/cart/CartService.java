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
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import org.testng.Assert;

import java.util.concurrent.Future;

/**
 * Created by jiefeng on 14-4-1.
 * Caller helper for cart APIs
 */
public class CartService {

    private static String commerceUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");
    private static LogHelper logger = new LogHelper(Cart.class);
    private static AsyncHttpClient asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());

    private CartService() {
    }

    public static Cart addCart(UserId userId, Cart cart) throws Exception {
        return addCart(userId, cart, 200);
    }

    public static Cart addCart(UserId userId, Cart cart, int expectedResponseCode) throws Exception {

        String requestBody = new String(new JsonMessageTranscoder().encode(cart));

        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts";

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
        if (expectedResponseCode == 200) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
                                                       },
                    nettyResponse.getResponseBody()
            ));
        }
        return null;
    }

    public static Cart getCart(UserId userId, CartId cartId) throws Exception {
        return getCart(userId, cartId, 200);
    }

    public static Cart getCart(UserId userId, CartId cartId, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = commerceUrl + "users/" +
                IdConverter.idToHexString(userId) + "/carts/" + IdConverter.idToHexString(cartId);

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
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
                                                       },
                    nettyResponse.getResponseBody()
            ));
        } else {
            return null;
        }
    }

    public static Cart getCartPrimary(UserId userId) throws Exception {
        return getCartPrimary(userId, 302);
    }

    public static Cart getCartPrimary(UserId userId, int expectedResponseCode) throws Exception {
        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts/primary";

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
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
            }, redirectResponse.getResponseBody()));
        }
        return null;
    }

    public static Results<Cart> getCartByName(UserId userId, String cartName) throws Exception {
        return getCartByName(userId, cartName, 200);
    }

    public static Results<Cart> getCartByName(UserId userId, String cartName, int expectedResponseCode) throws Exception {

        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId) + "/carts?cartName=" + cartName;

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
            Results<Cart> cartGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Cart>>() {
                                                                       },
                    nettyResponse.getResponseBody()
            );
            return cartGet;
        }
        return null;
    }

    public static Cart updateCart(UserId userId, CartId cartId, Cart cart) throws Exception {
        return updateCart(userId, cartId, cart, 200);
    }

    public static Cart updateCart(UserId userId, CartId cartId, Cart cart, int expectedResponseCode)
            throws Exception {
        String requestBody = new String(new JsonMessageTranscoder().encode(cart));

        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId)
                + "/carts/" + IdConverter.idToHexString(cartId);

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
        if (expectedResponseCode == 200) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
                                                       },
                    nettyResponse.getResponseBody()
            ));
        }
        return null;
    }

    public static Cart mergeCart(UserId userId, CartId cartId, Cart fromCart) throws Exception {
        return mergeCart(userId, cartId, fromCart, 200);
    }

    public static Cart mergeCart(UserId userId, CartId cartId, Cart fromCart, int expectedResponseCode)
            throws Exception {
        String requestBody = new String(new JsonMessageTranscoder().encode(fromCart));
        String cartEndpointUrl = commerceUrl + "users/" + IdConverter.idToHexString(userId)
                + "/carts/" + IdConverter.idToHexString(cartId) + "/merge";

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
        if (expectedResponseCode == 200) {
            return (new JsonMessageTranscoder().decode(new TypeReference<Cart>() {
                                                       },
                    nettyResponse.getResponseBody()
            ));
        }
        return null;
    }
}
