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

import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyResponse;
import java.util.HashMap;
import java.util.concurrent.Future;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Item related APIs
 */
public class ItemServiceImpl implements ItemService {

    private final String requestHeaderName = "Content-Type";
    private final String requestHeaderValue = "application/json";

    private final String catalogServerURL = "http://" +
            ConfigPropertiesHelper.instance().getProperty("catalog.host") +
            ":" +
            ConfigPropertiesHelper.instance().getProperty("catalog.port") +
            "/rest/items";

    private LogHelper logger = new LogHelper(ItemServiceImpl.class);
    private AsyncHttpClient asyncClient;

    public ItemServiceImpl() {
        asyncClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build());
    }

    public Item getItem(String itemId, HashMap<String, String> httpPara) throws Exception {

        String url = catalogServerURL + "/" + itemId;

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(requestHeaderName, requestHeaderValue);
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

        return itemGet;
    }

    public ResultList<Item> getItem(HashMap<String, String> httpPara) throws Exception {

        RequestBuilder reqBuilder = new RequestBuilder("GET");
        reqBuilder.addHeader(requestHeaderName, requestHeaderValue);
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

    public Item postItem(Item item) throws Exception {
        return null;
    }
    public Item updateItem(String itemId, Item item) throws Exception {
        return null;
    }

}
