/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Attribute related APIs
 */
public class ItemAttributeServiceImpl extends HttpClientBase implements ItemAttributeService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "item-attributes";
    private static ItemAttributeService instance;

    public static synchronized ItemAttributeService instance() {
        if (instance == null) {
            instance = new ItemAttributeServiceImpl();
        }
        return instance;
    }

    private ItemAttributeServiceImpl() {
    }

    public ItemAttribute getItemAttribute(Long attributeId) throws Exception {
        return getItemAttribute(attributeId, 200);
    }

    public ItemAttribute getItemAttribute(Long attributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemAttributeId.class, attributeId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        ItemAttribute attributeGet = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(ItemAttributeId.class, attributeGet.getId());
        Master.getInstance().addItemAttribute(attributeRtnId, attributeGet);
        return attributeGet;
    }

    public Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara) throws Exception {
        return getItemAttributes(httpPara, 200);
    }

    public Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<ItemAttribute> itemAttributeResults =  new JsonMessageTranscoder().decode(
                new TypeReference<Results<ItemAttribute>>() {}, responseBody);

        for (ItemAttribute itemattribute : itemAttributeResults.getItems()){
            String attributeRtnId = IdConverter.idLongToHexString(ItemAttributeId.class, itemattribute.getId());
            Master.getInstance().addItemAttribute(attributeRtnId, itemattribute);
        }

        return itemAttributeResults;
    }

    public ItemAttribute postItemAttribute(ItemAttribute attribute) throws Exception {
        return postItemAttribute(attribute, 200);
    }

    public ItemAttribute postItemAttribute(ItemAttribute attribute, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute, expectedResponseCode);
        ItemAttribute attributePost = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(ItemAttributeId.class, attributePost.getId());
        Master.getInstance().addItemAttribute(attributeRtnId, attributePost);
        return attributePost;
    }

    public ItemAttribute updateItemAttribute(ItemAttribute attribute) throws Exception {
        return updateItemAttribute(attribute, 200);
    }

    public ItemAttribute updateItemAttribute(ItemAttribute attribute, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(ItemAttributeId.class,
                attribute.getId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, attribute, expectedResponseCode);
        ItemAttribute itemAttributePut = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {},
                responseBody);
        String itemAttributeRtnId = IdConverter.idLongToHexString(ItemAttributeId.class,
                itemAttributePut.getId());
        Master.getInstance().addItemAttribute(itemAttributeRtnId, itemAttributePut);

        return itemAttributePut;
    }

    public void deleteItemAttribute(Long itemAttributeId) throws Exception {
        deleteItemAttribute(itemAttributeId, 204);
    }

    public void deleteItemAttribute(Long itemAttributeId, int expectedResponseCode) throws Exception {
        String strItemAttributeId = IdConverter.idLongToHexString(ItemAttributeId.class, itemAttributeId);
        String url = catalogServerURL + "/" + strItemAttributeId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeItemAttribute(strItemAttributeId);
    }

}
