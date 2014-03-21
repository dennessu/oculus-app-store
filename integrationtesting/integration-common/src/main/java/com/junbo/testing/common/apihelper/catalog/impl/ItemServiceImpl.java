/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.common.id.ItemId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.IdConverter;
import com.junbo.testing.common.libs.LogHelper;

import com.junbo.testing.common.libs.RestUrl;
import com.ning.http.client.*;
import com.ning.http.client.providers.netty.NettyResponse;
import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Item related APIs
 */
public class ItemServiceImpl implements ItemService {

    private final String catalogServerURL = RestUrl.getRestUrl("catalog") +
            "items";
    private static String defaultItemFileName = "defaultItem";
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private LogHelper logger = new LogHelper(ItemServiceImpl.class);
    private AsyncHttpClient asyncClient;

    private static ItemService instance;

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
        return getItem(itemId, httpPara, 200);
    }

    public String getItem(String itemId, HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

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
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Item itemGet = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemGet.getId());
        Master.getInstance().addItem(itemRtnId, itemGet);

        return itemRtnId;
    }

    public List<String> getItem(HashMap<String, String> httpPara) throws Exception {
        return getItem(httpPara, 200);
    }

    public List<String> getItem(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

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
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        ResultList<Item> itemGet = new JsonMessageTranscoder().decode(new TypeReference<ResultList<Item>>() {},
                nettyResponse.getResponseBody());

        List<String> listItemId = new ArrayList<>();
        for (Item item : itemGet.getResults()){
            String itemRtnId = IdConverter.idLongToHexString(ItemId.class, item.getId());
            Master.getInstance().addItem(itemRtnId, item);
            listItemId.add(itemRtnId);
        }

        return listItemId;
    }

    public String postDefaultItem() throws Exception {

        String resourceLocation = String.format("classpath:testItems/%s.json", defaultItemFileName);
        Resource resource = resolver.getResource(resourceLocation);
        Assert.assertNotNull(resource);

        BufferedReader br = new BufferedReader(new FileReader(resource.getFile().getPath()));
        StringBuilder strDefaultItem = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strDefaultItem.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
        }

        Item itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                strDefaultItem.toString());
        UserService us = UserServiceImpl.instance();
        itemForPost.setOwnerId(Long.valueOf(us.PostUser()));

        RequestBuilder reqBuilder = new RequestBuilder("POST");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(catalogServerURL);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(itemForPost));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);

        itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemForPost.getId());
        Master.getInstance().addItem(itemRtnId, itemForPost);

        return itemRtnId;
    }

    public String postItem(Item item) throws Exception {
        return postItem(item, 200);
    }

    public String postItem(Item item, int expectedResponseCode) throws Exception {

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

        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPost.getId());
        Master.getInstance().addItem(itemRtnId, itemPost);

        return itemPost.getId().toString();
    }

    public String updateItem(String itemId, Item item) throws Exception {
        return updateItem(itemId, item, 200);
    }

    public String updateItem(String itemId, Item item, int expectedResponseCode) throws Exception {
        return null;
    }

}
