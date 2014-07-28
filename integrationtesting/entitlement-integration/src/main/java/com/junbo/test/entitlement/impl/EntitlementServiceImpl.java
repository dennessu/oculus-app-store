/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement.impl;

import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.entitlement.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.model.Results;

/**
 * @author Jason
 * Time: 7/28/2014
 * The implementation for entitlement related APIs
 */
public class EntitlementServiceImpl  extends HttpClientBase implements EntitlementService {

    private String entitlementUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1") + "entitlements";
    private static EntitlementService instance;
    private boolean isServiceScope = true;

    public static synchronized EntitlementService instance() {
        if (instance == null) {
            instance = new EntitlementServiceImpl();
        }
        return instance;
    }

    private EntitlementServiceImpl() {
        componentType = ComponentType.ENTITLEMENT;
    }

    public Entitlement grantEntitlement(Entitlement entitlement) throws Exception {
        return grantEntitlement(entitlement, 200);
    }

    public Entitlement grantEntitlement(Entitlement entitlement, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, entitlementUrl, entitlement, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {}, responseBody);
    }

    public Entitlement getEntitlement(String entitlementId) throws Exception {
        return getEntitlement(entitlementId, 200);
    }

    public Entitlement getEntitlement(String entitlementId, int expectedResponseCode) throws Exception {
        String entitlementGetUrl = entitlementUrl + "/" + entitlementId;
        String responseBody = restApiCall(HTTPMethod.GET, entitlementGetUrl, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {}, responseBody);
    }

    public Entitlement updateEntitlement(String entitlementId, Entitlement entitlement) throws Exception {
        return updateEntitlement(entitlementId, entitlement, 200);
    }

    public Entitlement updateEntitlement(String entitlementId, Entitlement entitlement, int expectedResponseCode)
            throws Exception {
        String entitlementPutUrl = entitlementUrl + "/" + entitlementId;
        String responseBody = restApiCall(HTTPMethod.PUT, entitlementPutUrl, entitlement, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {}, responseBody);
    }

    public void deleteEntitlement(String entitlementId) throws Exception {
        deleteEntitlement(entitlementId, 204);
    }

    public void deleteEntitlement(String entitlementId, int expectedResponseCode) throws Exception {
        String entitlementDeleteUrl = entitlementUrl + "/" + entitlementId;
        restApiCall(HTTPMethod.DELETE, entitlementDeleteUrl, null, expectedResponseCode, isServiceScope);
    }

    public Results<Entitlement> getEntitlements(String userId) throws Exception {
        return getEntitlements(userId, 200);
    }

    public Results<Entitlement> getEntitlements(String userId, int expectedResponseCode) throws Exception {
        String entitlementGetUrl = entitlementUrl + "?userId=" + userId;
        String responseBody = restApiCall(HTTPMethod.GET, entitlementGetUrl, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Entitlement>>() {}, responseBody);
    }
}
