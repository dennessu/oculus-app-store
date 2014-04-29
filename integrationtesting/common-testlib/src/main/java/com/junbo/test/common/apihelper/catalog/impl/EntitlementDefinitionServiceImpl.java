/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.catalog.impl;

import com.junbo.test.common.apihelper.catalog.EntitlementDefinitionService;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.EnumHelper;
import com.junbo.test.common.libs.RestUrl;
import com.junbo.common.model.Results;
import com.junbo.common.id.UserId;

import java.util.ArrayList;
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

    public String getEntitlementDefinition(String entitlementDefinitionId) throws Exception {
        return getEntitlementDefinition(entitlementDefinitionId, 200);
    }

    public String getEntitlementDefinition(String entitlementDefinitionId, int expectedResponseCode) throws Exception {

        String url = catalogServerURL + "/" + entitlementDefinitionId;
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        EntitlementDefinition entitlementDefinitionGet = new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {},
                responseBody);
        String entitlementDefinitionRtnId = IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                entitlementDefinitionGet.getEntitlementDefId());
        Master.getInstance().addEntitlementDefinition(entitlementDefinitionRtnId, entitlementDefinitionGet);

        return entitlementDefinitionRtnId;
    }

    public List<String> getEntitlementDefinitions(HashMap<String, String> httpPara) throws Exception {
        return getEntitlementDefinitions(httpPara, 200);
    }

    public List<String> getEntitlementDefinitions(HashMap<String, String> httpPara, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.GET, catalogServerURL, null, expectedResponseCode, httpPara);
        Results<EntitlementDefinition> entitlementDefinitionGet = new JsonMessageTranscoder().decode(
                new TypeReference<Results<EntitlementDefinition>>() {}, responseBody);
        List<String> listItemId = new ArrayList<>();
        for (EntitlementDefinition entitlementDefinition : entitlementDefinitionGet.getItems()){
            String entitlementDefinitionRtnId = IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                    entitlementDefinition.getEntitlementDefId());
            Master.getInstance().addEntitlementDefinition(entitlementDefinitionRtnId, entitlementDefinition);
            listItemId.add(entitlementDefinitionRtnId);
        }

        return listItemId;
    }

    public String postDefaultEntitlementDefinition(EnumHelper.EntitlementType entitlementDefinitionType) throws Exception {

        EntitlementDefinition entitlementDefinition = new EntitlementDefinition();
        entitlementDefinition.setType(entitlementDefinitionType.getType());
        String developerId = UserServiceImpl.instance().PostUser();
        entitlementDefinition.setDeveloperId(IdConverter.hexStringToId(UserId.class, developerId));
        entitlementDefinition.setGroup("");
        entitlementDefinition.setTag("");
        return postEntitlementDefinition(entitlementDefinition);
    }

    public String postEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception {
        return postEntitlementDefinition(entitlementDefinition, 200);
    }

    public String postEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode) throws Exception {

        String responseBody = restApiCall(HTTPMethod.POST, catalogServerURL, entitlementDefinition, expectedResponseCode);
        EntitlementDefinition entitlementDefinitionPost = new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {},
                responseBody);
        String entitlementDefinitionRtnId = IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                entitlementDefinitionPost.getEntitlementDefId());
        Master.getInstance().addEntitlementDefinition(entitlementDefinitionRtnId, entitlementDefinitionPost);

        return entitlementDefinitionRtnId;
    }

    public String updateEntitlementDefinition(EntitlementDefinition entitlementDefinition) throws Exception {
        return updateEntitlementDefinition(entitlementDefinition, 200);
    }

    public String updateEntitlementDefinition(EntitlementDefinition entitlementDefinition, int expectedResponseCode) throws Exception {

        String putUrl = catalogServerURL + "/" + IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                entitlementDefinition.getEntitlementDefId());
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, entitlementDefinition, expectedResponseCode);
        EntitlementDefinition entitlementDefinitionPut = new JsonMessageTranscoder().decode(new TypeReference<EntitlementDefinition>() {},
                responseBody);
        String entitlementDefinitionRtnId = IdConverter.idLongToHexString(EntitlementDefinitionId.class,
                entitlementDefinitionPut.getEntitlementDefId());
        Master.getInstance().addEntitlementDefinition(entitlementDefinitionRtnId, entitlementDefinitionPut);

        return entitlementDefinitionRtnId;
    }

    public void deleteEntitlementDefinition(String entitlementDefinitionId) throws Exception {
        deleteEntitlementDefinition(entitlementDefinitionId, 204);
    }

    public void deleteEntitlementDefinition(String entitlementDefinitionId, int expectedResponseCode) throws Exception {
        String url = catalogServerURL + "/" + entitlementDefinitionId;
        restApiCall(HTTPMethod.DELETE, url, null, expectedResponseCode);
        Master.getInstance().removeEntitlementDefinition(entitlementDefinitionId);
    }

}
