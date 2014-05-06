/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.model.Results;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;


import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Item related APIs
 */
public class ItemServiceImpl extends HttpClientBase implements ItemService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "items";
    private final String defaultItemFileName = "defaultItem";
    private static ItemService instance;

    public static synchronized ItemService instance() {
        if (instance == null) {
            instance = new ItemServiceImpl();
        }
        return instance;
    }

    private ItemServiceImpl() {
    }

    public Item getItem(Long itemId) throws Exception {
        return getItem(itemId, 200);
    }

    public Item getItem(Long itemId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, itemId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
    }

    public Results<Item> getItems(HashMap<String, List<String>> httpPara) throws Exception {
        return getItems(httpPara, 200);
    }

    public Results<Item> getItems(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Item>>() {}, responseBody);
    }

    public Item prepareItemEntity(String fileName) throws Exception {
        String strItem = readFileContent(String.format("testItems/%s.json", fileName));
        Item itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, strItem);
        UserService us = UserServiceImpl.instance();
        String developerId = us.PostUser();
        itemForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, developerId));
        return itemForPost;
    }

    public Item postDefaultItem(CatalogItemType itemType) throws Exception {
        Item itemForPost = prepareItemEntity(defaultItemFileName);
        itemForPost.setType(itemType.getItemType());

        return postItem(itemForPost);
    }

    public Item postItem(Item item) throws Exception {
        return postItem(item, 200);
    }

    public Item postItem(Item item, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, item, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
    }

    public Item updateItem(Item item) throws Exception {
        return updateItem(item, 200);
    }

    public Item updateItem(Item item, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, item.getItemId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, item, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
    }

    public void deleteItem(Long itemId) throws Exception {
        this.deleteItem(itemId, 204);
    }

    public void deleteItem(Long itemId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, itemId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }

}
