/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.libs.ConfigPropertiesHelper;
import com.junbo.testing.common.libs.LogHelper;

import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyResponse;
import sun.print.resources.serviceui_sv;

import java.util.HashMap;
import java.util.concurrent.Future;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Item related APIs
 */
public class ItemServiceImpl implements ItemService {

    private final String catalogServerURL = RestUrl.getRestUrl("catalog") +
            "/rest/items";

    private LogHelper logger = new LogHelper(ItemServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static ItemService instance = null;

    public static synchronized ItemService instance() {
        if (instance == null) {
            instance = new ItemServiceImpl();
        }
        return instance;
    }

    private ItemServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public String getItem(String itemId, HashMap<String, String> httpPara) throws Exception {

        String url = catalogServerURL + "/" + itemId;

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(url);
        if (!httpPara.isEmpty()) {
            for (String key: httpPara.keySet()) {
                reqBuilder.addQueryParameter(key, httpPara.get(key));
            }
        }
        Request req = reqBuilder.build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        Item itemGet = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        return itemGet.getId().toString();
    }

    public ResultList<Item> getItem(HashMap<String, String> httpPara) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        if (!httpPara.isEmpty()) {
            for (String key: httpPara.keySet()) {
                reqBuilder.addQueryParameter(key, httpPara.get(key));
            }
        }
        Request req = reqBuilder.build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        ResultList<Item> itemGet = new JsonMessageTranscoder().decode(new TypeReference<ResultList<Item>>() {},
                nettyResponse.getResponseBody());

        return itemGet;
    }

    public String postItem(Item item) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(item));
        Request req = reqBuilder.build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        Item itemPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        return itemPost.getId().toString();
    }

    public String postDefaultItem() throws Exception {

        Item itemforPost = new Item();

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(itemforPost));
        Request req = reqBuilder.build();

        logger.LogRequest(req);

        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();

        logger.LogResponse(nettyResponse);

        Item itemPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        return itemPost.getId().toString();

    }

    public String updateItem(String itemId, Item item) throws Exception {
        return null;
    }

}
