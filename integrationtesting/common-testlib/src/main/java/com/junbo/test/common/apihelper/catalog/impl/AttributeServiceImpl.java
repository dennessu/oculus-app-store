/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.common.model.Results;
import com.junbo.common.id.AttributeId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.test.common.apihelper.catalog.AttributeService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RestUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Attribute related APIs
 */
public class AttributeServiceImpl extends HttpClientBase implements AttributeService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "attributes";
    private static AttributeService instance;

    public static synchronized AttributeService instance() {
        if (instance == null) {
            instance = new AttributeServiceImpl();
        }
        return instance;
    }

    private AttributeServiceImpl() {
    }

    public String getAttribute(String attributeId) throws Exception {
        return getAttribute(attributeId, 200);
    }

    public String getAttribute(String attributeId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + attributeId;
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        Attribute attributeGet = new JsonMessageTranscoder().decode(new TypeReference<Attribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attributeGet.getId());
        Master.getInstance().addAttribute(attributeRtnId, attributeGet);

        return attributeRtnId;
    }

    public List<String> getAttribute(HashMap<String, String> httpPara) throws Exception {
        return getAttribute(httpPara, 200);
    }

    public List<String> getAttribute(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<Attribute> attributeGet = new JsonMessageTranscoder().decode(new TypeReference<Results<Attribute>>() {},
                responseBody);
        List<String> listAttrId = new ArrayList<>();
        for (Attribute attribute : attributeGet.getItems()){
            String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attribute.getId());
            Master.getInstance().addAttribute(attributeRtnId, attribute);
            listAttrId.add(attributeRtnId);
        }

        return listAttrId;
    }

    public String postAttribute(Attribute attribute) throws Exception {
        return postAttribute(attribute, 200);
    }

    public String postAttribute(Attribute attribute, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, attribute,expectedResponseCode);
        Attribute attributePost = new JsonMessageTranscoder().decode(new TypeReference<Attribute>() {},
                responseBody);
        String attributeRtnId = IdConverter.idLongToHexString(AttributeId.class, attributePost.getId());
        Master.getInstance().addAttribute(attributeRtnId, attributePost);

        return attributeRtnId;
    }
}
