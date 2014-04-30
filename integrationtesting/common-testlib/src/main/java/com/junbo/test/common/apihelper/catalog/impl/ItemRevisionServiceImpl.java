/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.catalog.ItemRevisionService;
import com.junbo.test.common.apihelper.catalog.ItemService;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.model.Results;

import java.util.ArrayList;
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

    public String getItemRevision(String revisionId) throws Exception {
        return getItemRevision(revisionId, 200);
    }

    public String getItemRevision(String revisionId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + revisionId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        ItemRevision itemRevision = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {},
                responseBody);
        String itemRevisionRtnId = IdConverter.idLongToHexString(ItemRevisionId.class, itemRevision.getItemId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevision);

        return itemRevisionRtnId;
    }

    public List<String> getItemRevisions(HashMap<String, String> httpPara) throws Exception {
        return getItemRevisions(httpPara, 200);
    }

    public List<String> getItemRevisions(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<ItemRevision> itemRevisionGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<ItemRevision>>() {}, responseBody);
        List<String> listItemRevisionId = new ArrayList<>();
        for (ItemRevision itemRevision : itemRevisionGet.getItems()){
            String itemRevisionRtnId = IdConverter.idLongToHexString(ItemRevisionId.class,
                    itemRevision.getRevisionId());
            Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevision);
            listItemRevisionId.add(itemRevisionRtnId);
        }

        return listItemRevisionId;
    }

    public String postItemRevision(ItemRevision itemRevision) throws Exception {
        return postItemRevision(itemRevision, 200);
    }

    public String postItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, itemRevision, expectedResponseCode);
        ItemRevision itemRevisionPost = new JsonMessageTranscoder().decode(
                new TypeReference<ItemRevision>() {}, responseBody);
        String itemRevisionRtnId = IdConverter.idLongToHexString(ItemRevisionId.class,
                itemRevisionPost.getRevisionId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevisionPost);

        return itemRevisionRtnId;
    }

    public String updateItemRevision(ItemRevision itemRevision) throws Exception {
        return updateItemRevision(itemRevision, 200);
    }

    public String updateItemRevision(ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemRevisionId.class,
                itemRevision.getRevisionId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, itemRevision, expectedResponseCode);
        ItemRevision itemRevisionPut = new JsonMessageTranscoder().decode(new TypeReference<ItemRevision>() {},
                responseBody);
        String itemRevisionRtnId = IdConverter.idLongToHexString(ItemRevisionId.class, itemRevisionPut.getRevisionId());
        Master.getInstance().addItemRevision(itemRevisionRtnId, itemRevisionPut);

        return itemRevisionRtnId;
    }

    public String postDefaultItemRevision() throws Exception {
        String itemId = ItemServiceImpl.instance().postDefaultItem(EnumHelper.CatalogItemType.getRandom());
        return postDefaultItemRevision(itemId);
    }

    public String postDefaultItemRevision(String itemId) throws Exception {
        if (itemId == null || itemId.length() == 0) {
            throw new Exception("ItemId is null or empty");
        }

        ItemService itemService = ItemServiceImpl.instance();
        String itemGetId = itemService.getItem(itemId);
        Item item = Master.getInstance().getItem(itemGetId);
        ItemRevision itemRevisionForPost;

        if (item.getType().equalsIgnoreCase(EnumHelper.CatalogItemType.DIGITAL.getItemType())) {
            itemRevisionForPost = prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        }
        else if (item.getType().equalsIgnoreCase(EnumHelper.CatalogItemType.STORED_VALUE.getItemType())) {
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

    public void deleteItemRevision(String itemRevisionId) throws Exception {
        deleteItemRevision(itemRevisionId, 204);
    }

    public void deleteItemRevision(String itemRevisionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + itemRevisionId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeItemRevision(itemRevisionId);
    }

}
