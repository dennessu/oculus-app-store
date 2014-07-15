/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 *         Time: 3/14/2014
 *         The implementation for Attribute related APIs
 */
public class ItemAttributeServiceImpl extends HttpClientBase implements ItemAttributeService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "item-attributes";
    private static ItemAttributeService instance;

    public static synchronized ItemAttributeService instance() {
        if (instance == null) {
            instance = new ItemAttributeServiceImpl();
        }
        return instance;
    }

    private ItemAttributeServiceImpl() {
    }

    public ItemAttribute getItemAttribute(String attributeId) throws Exception {
        return getItemAttribute(attributeId, 200);
    }

    public ItemAttribute getItemAttribute(String attributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idToUrlString(ItemAttributeId.class, attributeId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        ItemAttribute attributeGet = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {
        },
                responseBody);
        String attributeRtnId = IdConverter.idToUrlString(ItemAttributeId.class, attributeGet.getId());
        Master.getInstance().addItemAttribute(attributeRtnId, attributeGet);
        return attributeGet;
    }

    public Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara) throws Exception {
        return getItemAttributes(httpPara, 200);
    }

    public Results<ItemAttribute> getItemAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<ItemAttribute> itemAttributeResults = new JsonMessageTranscoder().decode(
                new TypeReference<Results<ItemAttribute>>() {
                }, responseBody);

        for (ItemAttribute itemAttribute : itemAttributeResults.getItems()) {
            String attributeRtnId = IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute.getId());
            Master.getInstance().addItemAttribute(attributeRtnId, itemAttribute);
        }

        return itemAttributeResults;
    }

    public ItemAttribute postDefaultItemAttribute() throws Exception {
        ItemAttribute attribute = new ItemAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        attribute.setType(CatalogItemAttributeType.GENRE.getType());

        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        attribute.setLocales(locales);

        return postItemAttribute(attribute, 200);
    }

    public ItemAttribute postItemAttribute(ItemAttribute attribute) throws Exception {
        return postItemAttribute(attribute, 200);
    }

    public ItemAttribute postItemAttribute(ItemAttribute attribute, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute, expectedResponseCode);
        ItemAttribute attributePost = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {
        },
                responseBody);
        String attributeRtnId = IdConverter.idToUrlString(ItemAttributeId.class, attributePost.getId());
        Master.getInstance().addItemAttribute(attributeRtnId, attributePost);
        return attributePost;
    }

    public ItemAttribute updateItemAttribute(String itemAttributeId, ItemAttribute attribute) throws Exception {
        return updateItemAttribute(itemAttributeId, attribute, 200);
    }

    public ItemAttribute updateItemAttribute(String itemAttributeId, ItemAttribute attribute, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idToUrlString(ItemAttributeId.class,
                itemAttributeId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, attribute, expectedResponseCode);
        ItemAttribute itemAttributePut = new JsonMessageTranscoder().decode(new TypeReference<ItemAttribute>() {
        },
                responseBody);
        String itemAttributeRtnId = IdConverter.idToUrlString(ItemAttributeId.class,
                itemAttributePut.getId());
        Master.getInstance().addItemAttribute(itemAttributeRtnId, itemAttributePut);

        return itemAttributePut;
    }

    public void deleteItemAttribute(String itemAttributeId) throws Exception {
        deleteItemAttribute(itemAttributeId, 204);
    }

    public void deleteItemAttribute(String itemAttributeId, int expectedResponseCode) throws Exception {
        String strItemAttributeId = IdConverter.idToUrlString(ItemAttributeId.class, itemAttributeId);
        String url = catalogServerURL + "/" + strItemAttributeId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeItemAttribute(strItemAttributeId);
    }

}
