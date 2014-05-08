/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.catalog.EntitlementDefinitionService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.catalog.enums.EntitlementType;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;
import com.junbo.common.id.UserId;

import java.util.HashMap;
import java.util.List;

/**
 @author Jason
  * Time: 3/14/2014
  * The implementation for Item related APIs
 */
public class EntitlementDefinitionServiceImpl extends HttpClientBase implements EntitlementDefinitionService {

    private final String catalogServerURL = RestUrl.getRestUrl(RestUrl.ComponentName.CATALOG) + "entitlement-definitions";
    private static EntitlementDefinitionService instance;

    public static synchronized EntitlementDefinitionService instance() {
        if (instance == null) {
            instance = new EntitlementDefinitionServiceImpl();
        }
        return instance;
    }

    private EntitlementDefinitionServiceImpl() {
    }

    public EntitlementDefinition getEntitlementDefinition(Long edId) throws Exception {
        return getEntitlementDefinition(edId, 200);
    }

    public EntitlementDefinition getEntitlementDefinition(Long edId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" +  IdConverter.idLongToHexString(EntitlementDefinitionId.class, edId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {},
                responseBody);
    }

    public Results<EntitlementDefinition> getEntitlementDefinitions(HashMap<String, List<String>> httpPara) throws Exception {
        return getEntitlementDefinitions(httpPara, 200);
    }

    public Results<EntitlementDefinition> getEntitlementDefinitions(HashMap<String, List<String>> httpPara, int expectedResponseCode)
            throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<EntitlementDefinition>>() {}, responseBody);
    }

    public EntitlementDefinition postDefaultEntitlementDefinition(EntitlementType entitlementDefinitionType) throws Exception {
        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setType(entitlementDefinitionType.getType());
        String developerId = UserServiceImpl.instance().PostUser();
        entitlementDefinition.setDeveloperId(IdConverter.hexStringToId(UserId.class, developerId));
        entitlementDefinition.setTag("");
        return postEntitlementDefinition(entitlementDefinition);
    }

    public EntitlementDefinition postEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception {
        return postEntitlementDefinition(entitlementDefinition, 200);
    }

    public EntitlementDefinition postEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode)
            throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, entitlementDefinition, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {}, responseBody);
    }

    public EntitlementDefinition updateEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception {
        return updateEntitlementDefinition(entitlementDefinition, 200);
    }

    public EntitlementDefinition updateEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode)
            throws Exception {
        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                entitlementDefinition.getEntitlementDefId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, entitlementDefinition, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {}, responseBody);
    }

    public void deleteEntitlementDefinition(Long edId) throws Exception {
        deleteEntitlementDefinition(edId, 204);
    }

    public void deleteEntitlementDefinition(Long edId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + IdConverter.idLongToHexString(EntitlementDefinitionId.class, edId);
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
    }

}
