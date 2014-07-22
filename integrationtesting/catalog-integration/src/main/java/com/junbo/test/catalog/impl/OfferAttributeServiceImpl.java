/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.enums.CatalogOfferAttributeType;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for offer attribute related APIs
 */
public class OfferAttributeServiceImpl extends HttpClientBase implements OfferAttributeService {

    private final String catalogServerURL = ConfigHelper.getSetting("defaultCatalogEndpointV1") + "offer-attributes";
    private static OfferAttributeService instance;

    public static synchronized OfferAttributeService instance() {
        if (instance == null) {
            instance = new OfferAttributeServiceImpl();
        }
        return instance;
    }

    private OfferAttributeServiceImpl() {
    }

    public OfferAttribute getOfferAttribute(String attributeId) throws Exception {
        return getOfferAttribute(attributeId, 200);
    }

    public OfferAttribute getOfferAttribute(String attributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idToUrlString(OfferAttributeId.class, attributeId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara) throws Exception {
        return getOfferAttributes(httpPara, 200);
    }

    public Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode)
            throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<OfferAttribute>>() {}, responseBody);
    }

    public OfferAttribute postDefaultOfferAttribute() throws Exception {
        OfferAttribute offerAttribute = new OfferAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);
        offerAttribute.setType(CatalogOfferAttributeType.getRandom());

        return postOfferAttribute(offerAttribute, 200);
    }

    public OfferAttribute postOfferAttribute(OfferAttribute attribute) throws Exception {
        return postOfferAttribute(attribute, 200);
    }

    public OfferAttribute postOfferAttribute(OfferAttribute attribute, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public OfferAttribute updateOfferAttribute(String offerAttributeId, OfferAttribute attribute) throws Exception {
        return updateOfferAttribute(offerAttributeId, attribute, 200);
    }

    public OfferAttribute updateOfferAttribute(String offerAttributeId, OfferAttribute attribute,
                                               int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idToUrlString(OfferAttributeId.class,
                offerAttributeId);
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, attribute, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public void deleteOfferAttribute(String offerAttributeId) throws Exception {
        deleteOfferAttribute(offerAttributeId, 204);
    }

    public void deleteOfferAttribute(String offerAttributeId, int expectedResponseCode) throws Exception {
        String strOfferAttributeId = IdConverter.idToUrlString(OfferAttributeId.class, offerAttributeId);
        String url = catalogServerURL + "/" + strOfferAttributeId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }
}
