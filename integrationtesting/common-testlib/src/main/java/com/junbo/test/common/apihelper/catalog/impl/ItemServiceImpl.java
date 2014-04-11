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
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.*;

import java.io.*;
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

    public String getItem(String itemId, HashMap<String, String> httpPara) throws Exception {
        return getItem(itemId, httpPara, 200);
    }

    public String getItem(String itemId, HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + itemId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode, httpPara);
        Item itemGet = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                responseBody);
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemGet.getId());
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
            String itemRtnId = IdConverter.idLongToHexString(ItemId.class, item.getId());
            Master.getInstance().addItem(itemRtnId, item);
            listItemId.add(itemRtnId);
        }

        return listItemId;
    }

    public Item prepareItemEntity(String fileName) throws Exception {

        String strItem = this.readFileContent(fileName);
        Item itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                strItem.toString());
        itemForPost.setName("testItem_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
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
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPost.getId());
        Master.getInstance().addItem(itemRtnId, itemPost);

        return itemRtnId;
    }

    public String updateItem(Item item) throws Exception {
        return updateItem(item, 200);
    }

    public String updateItem(Item item, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemId.class, item.getId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, item, expectedResponseCode);
        Item itemPut = new JsonMessageTranscoder().decode(new TypeReference<Item>() {},
                responseBody);
        String itemRtnId = IdConverter.idLongToHexString(ItemId.class, itemPut.getId());
        Master.getInstance().addItem(itemRtnId, itemPut);

        return itemRtnId;
    }

    private String readFileContent(String fileName) throws Exception {

        String resourceLocation = String.format("testItems/%s.json", fileName);
        InputStream inStream = ClassLoader.getSystemResourceAsStream(resourceLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(inStream));

        StringBuilder strItem = new StringBuilder();
        try {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                strItem.append(sCurrentLine + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null){
                br.close();
            }
            if (inStream != null) {
                inStream.close();
            }
        }

        return strItem.toString();
    }
}
