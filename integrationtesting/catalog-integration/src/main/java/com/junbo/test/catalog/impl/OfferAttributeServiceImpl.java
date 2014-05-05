/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Attribute related APIs
 */
public class OfferAttributeServiceImpl extends HttpClientBase implements OfferAttributeService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "offer-attributes";
    private static OfferAttributeService instance;

    public static synchronized OfferAttributeService instance() {
        if (instance == null) {
            instance = new OfferAttributeServiceImpl();
        }
        return instance;
    }

    private OfferAttributeServiceImpl() {
    }

    public OfferAttribute getOfferAttribute(Long attributeId) throws Exception {
        return getOfferAttribute(attributeId, 200);
    }

    public OfferAttribute getOfferAttribute(Long attributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferAttributeId.class, attributeId);
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara) throws Exception {
        return getOfferAttributes(httpPara, 200);
    }

    public Results<OfferAttribute> getOfferAttributes(HashMap<String, List<String>> httpPara, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<OfferAttribute>>() {}, responseBody);
    }

    public OfferAttribute postOfferAttribute(OfferAttribute attribute) throws Exception {
        return postOfferAttribute(attribute, 200);
    }

    public OfferAttribute postOfferAttribute(OfferAttribute attribute, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public OfferAttribute updateOfferAttribute(OfferAttribute attribute) throws Exception {
        return updateOfferAttribute(attribute, 200);
    }

    public OfferAttribute updateOfferAttribute(OfferAttribute attribute, int expectedResponseCode) throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferAttributeId.class,
                attribute.getId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, attribute, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {}, responseBody);
    }

    public void deleteOfferAttribute(Long offerAttributeId) throws Exception {
        deleteOfferAttribute(offerAttributeId, 204);
    }

    public void deleteOfferAttribute(Long offerAttributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferAttributeId.class, offerAttributeId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }
}
