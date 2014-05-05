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
import com.junbo.common.id.ItemRevisionId;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
  * @author Jason
  * Time: 4/17/2014
  * The implementation for Item Revision related APIs
 */
public class ItemRevisionServiceImpl extends HttpClientBase implements ItemRevisionService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "item-revisions";
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

    public ItemRevision getItemRevision(Long revisionId) throws Exception {
        return getItemRevision(revisionId, 200);
    }

    public ItemRevision getItemRevision(Long revisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemRevisionId.class, revisionId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {}, responseBody);
    }

    public Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara) throws Exception {
        return getItemRevisions(httpPara, 200);
    }

    public Results<ItemRevision> getItemRevisions(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<ItemRevision>>() {}, responseBody);
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

        if (item.getType().equalsIgnoreCase(CatalogItemType.DIGITAL.getItemType())) {
            itemRevisionForPost = prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        }
        else if (item.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            itemRevisionForPost = prepareItemRevisionEntity(defaultStoredValueItemRevisionFileName);
        }
        else {
            itemRevisionForPost = prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }

        itemRevisionForPost.setItemId(item.getItemId());
        itemRevisionForPost.setOwnerId(item.getOwnerId());

        return postItemRevision(itemRevisionForPost);
    }

    public ItemRevision prepareItemRevisionEntity(String fileName) throws Exception {

        String strItem = readFileContent(String.format("testItemRevisions/%s.json", fileName));
        ItemRevision itemRevisionForPost = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {},
                strItem);

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
        return new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {}, responseBody);
    }

    public ItemRevision updateItemRevision(ItemRevision itemRevision) throws Exception {
        return updateItemRevision(itemRevision, 200);
    }

    public ItemRevision updateItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemRevisionId.class,
                itemRevision.getRevisionId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, itemRevision, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {}, responseBody);
    }

    public void deleteItemRevision(Long itemRevisionId) throws Exception {
        deleteItemRevision(itemRevisionId, 204);
    }

    public void deleteItemRevision(Long itemRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemRevisionId.class, itemRevisionId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }

}
