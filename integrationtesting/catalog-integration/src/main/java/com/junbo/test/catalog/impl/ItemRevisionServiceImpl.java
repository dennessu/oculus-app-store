/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.blueprint.Master;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.model.Results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 *         Time: 4/17/2014
 *         The implementation for Item Revision related APIs
 */
public class ItemRevisionServiceImpl extends HttpClientBase implements ItemRevisionService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "item-revisions";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";
    private static ItemRevisionService instance;

    public static synchronized ItemRevisionService instance() {
        if (instance == null) {
            instance = new ItemRevisionServiceImpl();
        }
        return instance;
    }

    private ItemRevisionServiceImpl() {
    }

    public ItemRevision getItemRevision(String revisionId) throws Exception {
        return getItemRevision(revisionId, 200);
    }

    public ItemRevision getItemRevision(String revisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idToUrlString(ItemRevisionId.class, revisionId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        ItemRevision itemRevision = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {
        },
                responseBody);
        String itemRevisionRtnId = IdConverter.idToUrlString(ItemRevisionId.class, itemRevision.getRevisionId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevision);
        return itemRevision;
    }

    public Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara) throws Exception {
        return getItemRevisions(httpPara, 200);
    }

    public Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<ItemRevision> itemRevisionGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<ItemRevision>>() {
                }, responseBody);
        for (ItemRevision itemRevision : itemRevisionGet.getItems()) {
            String itemRevisionRtnId = IdConverter.idToUrlString(ItemRevisionId.class,
                    itemRevision.getRevisionId());
            Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevision);
        }

        return itemRevisionGet;
    }

    public ItemRevision postDefaultItemRevision() throws Exception {
        Item item = ItemServiceImpl.instance().postDefaultItem(CatalogItemType.getRandom());
        return postDefaultItemRevision(item);
    }

    public ItemRevision postDefaultItemRevision(Item item) throws Exception {
        if (item == null) {
            throw new Exception("Item is null");
        }

        ItemRevision itemRevisionForPost;

        if (item.getType().equalsIgnoreCase(CatalogItemType.APP.getItemType()) ||
                item.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.getItemType())) {
            itemRevisionForPost = prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        } else if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            itemRevisionForPost = prepareItemRevisionEntity(defaultStoredValueItemRevisionFileName);
        } else {
            itemRevisionForPost = prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }

        //prepare IapHostItemIds
        if (itemRevisionForPost.getDistributionChannels().contains("INAPP")) {
            Item iapHostItem = ItemServiceImpl.instance().postDefaultItem(CatalogItemType.APP, item.getOwnerId());
            List<String> iapHostItemIds = new ArrayList<>();
            iapHostItemIds.add(iapHostItem.getItemId());
            itemRevisionForPost.setIapHostItemIds(iapHostItemIds);
        }

        itemRevisionForPost.setItemId(item.getItemId());
        itemRevisionForPost.setOwnerId(item.getOwnerId());

        return postItemRevision(itemRevisionForPost);
    }

    public ItemRevision prepareItemRevisionEntity(String fileName) throws Exception {

        String strItem = readFileContent(String.format("testItemRevisions/%s.json", fileName));
        ItemRevision itemRevisionForPost = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {},strItem);

        //set locales
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        HashMap<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("en_US", itemRevisionLocaleProperties);
        itemRevisionForPost.setLocales(locales);

        return itemRevisionForPost;
    }

    public ItemRevision postItemRevision(ItemRevision itemRevision) throws Exception {
        return postItemRevision(itemRevision, 200);
    }

    public ItemRevision postItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, itemRevision, expectedResponseCode);
        ItemRevision itemRevisionPost = new JsonMessageTranscoder().decode(
                new TypeReference<ItemRevision>() {
                }, responseBody);
        String itemRevisionRtnId = IdConverter.idToUrlString(ItemRevisionId.class,
                itemRevisionPost.getRevisionId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevisionPost);

        return itemRevisionPost;
    }

    public ItemRevision updateItemRevision(String itemRevisionId, ItemRevision itemRevision) throws Exception {
        return updateItemRevision(itemRevisionId, itemRevision, 200);
    }

    public ItemRevision updateItemRevision(String itemRevisionId, ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idToUrlString(ItemRevisionId.class,
                itemRevisionId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, itemRevision, expectedResponseCode);
        ItemRevision itemRevisionPut = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {
        },
                responseBody);
        String itemRevisionRtnId = IdConverter.idToUrlString(ItemRevisionId.class, itemRevisionPut.getRevisionId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevisionPut);
        return itemRevisionPut;
    }

    public void deleteItemRevision(String itemRevisionId) throws Exception {
        deleteItemRevision(itemRevisionId, 204);
    }

    public void deleteItemRevision(String itemRevisionId, int expectedResponseCode) throws Exception {
        String strItemRevisionId = IdConverter.idToUrlString(ItemRevisionId.class, itemRevisionId);
        String url = catalogServerURL + "/" + strItemRevisionId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeItemRevision(strItemRevisionId);
    }

}
