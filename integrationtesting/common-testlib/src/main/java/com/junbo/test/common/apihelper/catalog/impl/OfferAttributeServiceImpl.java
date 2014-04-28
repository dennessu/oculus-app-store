/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.test.common.apihelper.catalog.OfferAttributeService;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;

import java.util.ArrayList;
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

    public String getOfferAttribute(String attributeId) throws Exception {
        return getOfferAttribute(attributeId, 200);
    }

    public String getOfferAttribute(String attributeId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + attributeId;
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        OfferAttribute attributeGet = new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(OfferAttributeId.class, attributeGet.getId());
        Master.getInstance().addOfferAttribute(attributeRtnId, attributeGet);

        return attributeRtnId;
    }

    public List<String> getOfferAttributes(HashMap<String, String> httpPara) throws Exception {
        return getOfferAttributes(httpPara, 200);
    }

    public List<String> getOfferAttributes(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<OfferAttribute> attributeGet = new JsonMessageTranscoder().decode(new TypeReference<Results<OfferAttribute>>() {},
                responseBody);
        List<String> listAttrId = new ArrayList<>();
        for (OfferAttribute itemattribute : attributeGet.getItems()){
            String attributeRtnId = IdConverter.idLongToHexString(OfferAttributeId.class, itemattribute.getId());
            Master.getInstance().addOfferAttribute(attributeRtnId, itemattribute);
            listAttrId.add(attributeRtnId);
        }

        return listAttrId;
    }

    public String postOfferAttribute(OfferAttribute attribute) throws Exception {
        return postOfferAttribute(attribute, 200);
    }

    public String postOfferAttribute(OfferAttribute attribute, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute, expectedResponseCode);
        OfferAttribute attributePost = new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(OfferAttributeId.class, attributePost.getId());
        Master.getInstance().addOfferAttribute(attributeRtnId, attributePost);

        return attributeRtnId;
    }

    public String updateOfferAttribute(OfferAttribute attribute) throws Exception {
        return updateOfferAttribute(attribute, 200);
    }

    public String updateOfferAttribute(OfferAttribute attribute, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(OfferAttributeId.class,
                attribute.getId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, attribute, expectedResponseCode);
        OfferAttribute offerAttributePut = new JsonMessageTranscoder().decode(new TypeReference<OfferAttribute>() {},
                responseBody);
        String offerAttributeRtnId = IdConverter.idLongToHexString(OfferAttributeId.class,
                offerAttributePut.getId());
        Master.getInstance().addOfferAttribute(offerAttributeRtnId, offerAttributePut);

        return offerAttributeRtnId;
    }

    public void deleteOfferAttribute(String offerAttributeId) throws Exception {
        deleteOfferAttribute(offerAttributeId, 204);
    }

    public void deleteOfferAttribute(String offerAttributeId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + offerAttributeId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeOfferAttribute(offerAttributeId);
    }
}
