/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.common.LocalizableProperty;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.common.id.ItemId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;

import java.util.ArrayList;
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

    public String getItem(String itemId) throws Exception {
        return getItem(itemId, 200);
    }

    public String getItem(String itemId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + itemId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        Item itemGet = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                responseBody);
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemGet.getItemId());
        Master.getInstance().addItem(itemRtnId, itemGet);

        return itemRtnId;
    }

    public List<String> getItem(HashMap<String, String> httpPara) throws Exception {
        return getItem(httpPara, 200);
    }

    public List<String> getItem(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<Item> itemGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Item>>() {},
                responseBody);
        List<String> listItemId = new ArrayList<>();
        for (Item item : itemGet.getItems()){
            String itemRtnId = IdConverter.idLongToHexString(ItemId.class, item.getItemId());
            Master.getInstance().addItem(itemRtnId, item);
            listItemId.add(itemRtnId);
        }

        return listItemId;
    }

    public Item prepareItemEntity(String fileName) throws Exception {

        String strItem = readFileContent(String.format("testItems/%s.json", fileName));
        Item itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                strItem.toString());
        LocalizableProperty itemName = new LocalizableProperty();
        itemName.set("en_US", "testItem_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        itemForPost.setName(itemName);
        UserService us = UserServiceImpl.instance();
        String developerId = us.PostUser();
        itemForPost.setOwnerId(IdConverter.hexStringToId(UserId.class, developerId));

        return itemForPost;
    }

    public String postDefaultItem(EnumHelper.CatalogItemType itemType) throws Exception {

        Item itemForPost = prepareItemEntity(defaultItemFileName);
        itemForPost.setType(itemType.getItemType());

        return postItem(itemForPost);
    }

    public String postItem(Item item) throws Exception {
        return postItem(item, 200);
    }

    public String postItem(Item item, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, item, expectedResponseCode);
        Item itemPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                responseBody);
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPost.getItemId());
        Master.getInstance().addItem(itemRtnId, itemPost);

        return itemRtnId;
    }

    public String updateItem(Item item) throws Exception {
        return updateItem(item, 200);
    }

    public String updateItem(Item item, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, item.getItemId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, item, expectedResponseCode);
        Item itemPut = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                responseBody);
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPut.getItemId());
        Master.getInstance().addItem(itemRtnId, itemPut);

        return itemRtnId;
    }

}
