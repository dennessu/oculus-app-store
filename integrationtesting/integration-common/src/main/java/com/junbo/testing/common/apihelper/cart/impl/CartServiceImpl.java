package com.junbo.testing.common.apihelper.cart.impl;

import com.junbo.cart.spec.model.Cart;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.apihelper.cart.CartService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.util.concurrent.Future;

/**
 * Created by jiefeng on 14-3-19.
 * Caller helper for cart APIs
 */
public class CartServiceImpl implements CartService{

    private static String cartUrl = RestUrl.getRestUrl("cart");

    private LogHelper logger = new LogHelper(CartServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static CartService instance = null;

    public static synchronized CartService getInstance(){
        if (instance == null) {
            instance = new CartServiceImpl();
        }
        return instance;
    }

    private CartServiceImpl(){
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String addCart(String userId, Cart cart) throws Exception{
        return addCart(userId, cart, 200);

    }

    public String addCart(String userId, Cart cart, int expectedResponseCode) throws Exception{

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
        Assert.assertEquals(expectedResponseCode,nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addCart(rtnCart.getId().toString(),rtnCart);
        return rtnCart.getId().toString();
    }

    public String  getCart(String userId, String cartId) throws Exception{
        return getCart(userId, cartId,200);
    }

    public String  getCart(String userId, String cartId, int expectedResponseCode) throws Exception{
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
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addCart(rtnCart.getId().toString(),rtnCart);
        return rtnCart.getId().toString();
    }

    public String getCartPrimary(String userId) throws Exception{
        return getCartPrimary(userId, 200);
    }

    public String getCartPrimary(String userId, int expectedResponseCode) throws Exception{
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
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addCart(rtnCart.getId().toString(),rtnCart);
        return rtnCart.getId().toString();
    }

    public String getCartByName(String userId, String cartName) throws Exception{
        return null;
    }

    public String getCartByName(String userId, String cartName, int expectedResponseCode) throws Exception{

        String cartEndpointUrl = cartUrl + "users/" + userId + "/carts?" + cartName;

        Request req = new RequestBuilder("GET")
                .setUrl(cartEndpointUrl)
                .addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue)
                .build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode,nettyResponse.getStatusCode());
        Cart rtnCart = new JsonMessageTranscoder().decode(new TypeReference<Cart>() {},
                nettyResponse.getResponseBody());
        Master.getInstance().addCart(rtnCart.getId().toString(),rtnCart);
        return rtnCart.getId().toString();
    }

    public String updateCart(String userId, String cartId, Cart cart) throws Exception{
        return null;
    }

    public String mergeCart(String userId, String cartId, String fromCartId) throws Exception{
        return null;
    }

    public String addOffer(String userId, String cartId, String offerId) throws Exception{
        return null;
    }

    public String updateOffer(String userId, String cartId, String offerItemId) throws Exception{
        return null;
    }

    public String removeOffer(String userId, String cartId, String offerItemId) throws Exception{
        return null;
    }

    public String addCoupon(String userId, String cartId, String CoupleId) throws Exception{
        return null;
    }

    public String removeCouple(String userId, String cartId, String coupleItemID) throws Exception{
        return null;
    }
}
