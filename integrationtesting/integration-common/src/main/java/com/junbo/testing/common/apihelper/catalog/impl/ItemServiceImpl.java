/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.common.id.ItemId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;

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

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "items";
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

        Results<Item> itemGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Item>>() {},
                nettyResponse.getResponseBody());

        List<String> listItemId = new ArrayList<>();
        for (Item item : itemGet.getItems()){
            String itemRtnId = IdConverter.idLongToHexString(ItemId.class, item.getId());
            Master.getInstance().addItem(itemRtnId, item);
            listItemId.add(itemRtnId);
        }

        return listItemId;
    }

    public Item prepareItemEntity(String fileName, boolean isPhysical) throws Exception {

        String resourceLocation = String.format("classpath:testItems/%s.json", fileName);
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
        itemForPost.setName("testItem_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        UserService us = UserServiceImpl.instance();
        String developerId = us.PostUser();
        itemForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, developerId));

        //To post a digital or physical item:
        if (isPhysical) {
            itemForPost.setType(EnumHelper.CatalogItemType.PHYSICAL.getItemType());
        }
        else {
            if ((int) (Math.random() * 2) == 1) {
                itemForPost.setType(EnumHelper.CatalogItemType.APP.getItemType());
            }
            else {
                itemForPost.setType(EnumHelper.CatalogItemType.IAP.getItemType());
            }
        }

        return itemForPost;
    }

    public String postDefaultItem(boolean isPhysical) throws Exception {

        Item itemForPost = prepareItemEntity(defaultItemFileName, isPhysical);

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
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Item itemPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPost.getId());
        Master.getInstance().addItem(itemRtnId, itemPost);

        return itemRtnId;
    }

    public String updateItem(Item item) throws Exception {
        return updateItem(item, 200);
    }

    public String updateItem(Item item, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, item.getId());
        RequestBuilder reqBuilder = new RequestBuilder("PUT");
        reqBuilder.addHeader(RestUrl.requestHeaderName, RestUrl.requestHeaderValue);
        reqBuilder.setUrl(putUrl);
        reqBuilder.setBody(new JsonMessageTranscoder().encode(item));
        Request req = reqBuilder.build();

        logger.LogRequest(req);
        Future future = asyncClient.prepareRequest(req).execute();
        NettyResponse nettyResponse = (NettyResponse) future.get();
        logger.LogResponse(nettyResponse);
        Assert.assertEquals(expectedResponseCode, nettyResponse.getStatusCode());

        Item itemPut = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                nettyResponse.getResponseBody());

        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPut.getId());
        Master.getInstance().addItem(itemRtnId, itemPut);

        return itemRtnId;
    }

}
