/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.catalog.ItemService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 *         Time: 3/14/2014
 *         The implementation for Item related APIs
 */
public class ItemServiceImpl extends HttpClientBase implements ItemService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "items";
    private final String defaultItemFileName = "defaultItem";
    private static ItemService instance;

    public static synchronized ItemService instance() {
        if (instance == null) {
            instance = new ItemServiceImpl();
        }
        return instance;
    }

    private ItemServiceImpl() {
        componentType = ComponentType.CATALOG;
    }

    public Item getItem(String itemId) throws Exception {
        return getItem(itemId, 200);
    }

    public Item getItem(String itemId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + itemId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        Item itemGet = new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
        Master.getInstance().addItem(itemGet.getItemId(), itemGet);
        return itemGet;
    }

    public Results<Item> getItems(HashMap<String, List<String>> httpPara) throws Exception {
        return getItems(httpPara, 200);
    }

    public Results<Item> getItems(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<Item> itemGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Item>>() {}, responseBody);
        for (Item item : itemGet.getItems()) {
            Master.getInstance().addItem(item.getItemId(), item);
        }
        return itemGet;
    }

    public Item prepareItemEntity(String fileName) throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        return prepareItemEntity(fileName, organizationService.postDefaultOrganization().getId());
    }

    public Item prepareItemEntity(String fileName, OrganizationId organizationId) throws Exception {
        String strItem = readFileContent(String.format("testItems/%s.json", fileName));
        Item itemForPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, strItem);
        itemForPost.setOwnerId(organizationId);
        return itemForPost;
    }

    public Item postDefaultItem(CatalogItemType itemType) throws Exception {
        Item itemForPost = prepareItemEntity(defaultItemFileName);
        itemForPost.setType(itemType.getItemType());

        return postItem(itemForPost);
    }

    public Item postDefaultItem(CatalogItemType itemType, OrganizationId organizationId) throws Exception {
        Item itemForPost = prepareItemEntity(defaultItemFileName, organizationId);
        itemForPost.setType(itemType.getItemType());

        return postItem(itemForPost);
    }

    public Item postItem(Item item) throws Exception {
        return postItem(item, 200);
    }

    public Item postItem(Item item, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, item, expectedResponseCode);
        Item itemPost = new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
        Master.getInstance().addItem(itemPost.getItemId(), itemPost);
        return itemPost;
    }

    public Item updateItem(String itemId, Item item) throws Exception {
        return updateItem(itemId, item, 200);
    }

    public Item updateItem(String itemId, Item item, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + itemId;
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, item, expectedResponseCode);
        Item itemPut = new JsonMessageTranscoder().decode(new TypeReference<Item>() {}, responseBody);
        Master.getInstance().addItem(itemPut.getItemId(), itemPut);
        return itemPut;
    }

    public void deleteItem(String itemId) throws Exception {
        this.deleteItem(itemId, 204);
    }

    public void deleteItem(String itemId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + itemId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeItem(itemId);
    }

}
