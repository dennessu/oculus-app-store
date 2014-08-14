/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.entitlement.impl;

import com.junbo.common.id.ItemId;
import com.junbo.entitlement.spec.model.DownloadUrlResponse;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.entitlement.EntitlementService;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.model.Results;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * @author Jason
 * Time: 7/28/2014
 * The implementation for entitlement related APIs
 */
public class EntitlementServiceImpl extends HttpClientBase implements EntitlementService {

    private String entitlementUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1") + "entitlements";
    private String itemBinariesUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1") + "item-binary";
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
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
        }, responseBody);
    }

    public Entitlement getEntitlement(String entitlementId) throws Exception {
        return getEntitlement(entitlementId, 200);
    }

    public Entitlement getEntitlement(String entitlementId, int expectedResponseCode) throws Exception {
        String entitlementGetUrl = entitlementUrl + "/" + entitlementId;
        String responseBody = restApiCall(HTTPMethod.GET, entitlementGetUrl, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
        }, responseBody);
    }

    public Entitlement updateEntitlement(String entitlementId, Entitlement entitlement) throws Exception {
        return updateEntitlement(entitlementId, entitlement, 200);
    }

    public Entitlement updateEntitlement(String entitlementId, Entitlement entitlement, int expectedResponseCode)
            throws Exception {
        String entitlementPutUrl = entitlementUrl + "/" + entitlementId;
        String responseBody = restApiCall(HTTPMethod.PUT, entitlementPutUrl, entitlement, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Entitlement>() {
        }, responseBody);
    }

    public void deleteEntitlement(String entitlementId) throws Exception {
        deleteEntitlement(entitlementId, 204);
    }

    public void deleteEntitlement(String entitlementId, int expectedResponseCode) throws Exception {
        String entitlementDeleteUrl = entitlementUrl + "/" + entitlementId;
        restApiCall(HTTPMethod.DELETE, entitlementDeleteUrl, null, expectedResponseCode, isServiceScope);
    }

    @Override
    public String getDownloadUrl(String entitlementId, String itemId, String platform) throws Exception {
        return getDownloadUrl(entitlementId, itemId, platform, 200);
    }

    @Override
    public String getDownloadUrl(String entitlementId, String itemId, String platform, int expectedResponseCode) throws Exception {
        String getDownloadUrl = itemBinariesUrl + "/" + itemId + "?entitlementId=" + entitlementId + "&platform=" + platform;
        String responseBody = restApiCall(HTTPMethod.GET, getDownloadUrl, null, expectedResponseCode, isServiceScope);
        try {
            DownloadUrlResponse response = new JsonMessageTranscoder().decode(new TypeReference<DownloadUrlResponse>() {
            }, responseBody);
            return response.getRedirectUrl();
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    public String getDownloadUrlForItemRevision(String itemRevisionId, String itemId, String platform) throws Exception {
        return getDownloadUrlForItemRevision(itemRevisionId, itemId, platform, 200);
    }

    @Override
    public String getDownloadUrlForItemRevision(String itemRevisionId, String itemId, String platform, int expectedResponseCode) throws Exception {
        String getDownloadUrl = itemBinariesUrl + "/" + itemId + "?itemRevisionId=" + itemRevisionId + "&platform=" + platform;
        String responseBody = restApiCall(HTTPMethod.GET, getDownloadUrl, null, expectedResponseCode, isServiceScope);
        try {
            DownloadUrlResponse response = new JsonMessageTranscoder().decode(new TypeReference<DownloadUrlResponse>() {
            }, responseBody);
            return response.getRedirectUrl();
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    public Results<Entitlement> searchEntitlements(EntitlementSearchParam param, String cursor, Integer count) throws Exception {
        return searchEntitlements(param, cursor, count, 200);
    }

    @Override
    public Results<Entitlement> searchEntitlements(EntitlementSearchParam param, String cursor, Integer count, int expectedResponseCode) throws Exception {
        StringBuilder entitlementSearchUrl = new StringBuilder(entitlementUrl + "?userId=" + IdConverter.idToHexString(param.getUserId()));
        if (param.getIsActive() != null) {
            entitlementSearchUrl.append("&isActive=" + param.getIsActive());
        }
        if (param.getIsBanned() != null) {
            entitlementSearchUrl.append("&isSuspended=" + param.getIsBanned());
        }
        if (param.getType() != null) {
            entitlementSearchUrl.append("&type=" + param.getType());
        }
        if (param.getHostItemId() != null) {
            entitlementSearchUrl.append("&hostItemId=" + param.getHostItemId().getValue());
        }
        if (!CollectionUtils.isEmpty(param.getItemIds())) {
            for (ItemId itemId : param.getItemIds()) {
                entitlementSearchUrl.append("&itemIds=" + itemId.getValue());
            }
        }
        if (!StringUtils.isEmpty(param.getStartGrantTime())) {
            entitlementSearchUrl.append("&startGrantTime=" + param.getStartGrantTime());
        }
        if (!StringUtils.isEmpty(param.getEndGrantTime())) {
            entitlementSearchUrl.append("&endGrantTime=" + param.getEndGrantTime());
        }
        if (!StringUtils.isEmpty(param.getStartExpirationTime())) {
            entitlementSearchUrl.append("&startExpirationTime=" + param.getStartExpirationTime());
        }
        if (!StringUtils.isEmpty(param.getEndExpirationTime())) {
            entitlementSearchUrl.append("&endExpirationTime=" + param.getEndExpirationTime());
        }
        if (!StringUtils.isEmpty(param.getLastModifiedTime())) {
            entitlementSearchUrl.append("&lastModifiedTime=" + param.getLastModifiedTime());
        }
        if (!StringUtils.isEmpty(cursor)) {
            entitlementSearchUrl.append("&bookmark=" + cursor);
        }
        if (count != null) {
            entitlementSearchUrl.append("&count=" + count);
        }
        String responseBody = restApiCall(HTTPMethod.GET, entitlementSearchUrl.toString(), expectedResponseCode);
        try {
            return new JsonMessageTranscoder().decode(new TypeReference<Results<Entitlement>>() {
            }, responseBody);
        } catch (Exception ignore) {
        }
        return null;
    }

    @Override
    public void getBinariesUrl(Entitlement entitlement) throws Exception {
        if (entitlement.getBinaries() == null) {
            return;
        }
        Set<String> key = entitlement.getBinaries().keySet();

        for (Iterator it = key.iterator(); it.hasNext(); ) {
            String id = (String) it.next();
            String url = ConfigHelper.getSetting("defaultCommerceEndpointV1");
            String host = url.replace("/v1/", "");
            restApiCall(HTTPMethod.GET, host + entitlement.getBinaries().get(id), 200);
        }
    }

    public Results<Entitlement> getEntitlements(String userId) throws Exception {
        return getEntitlements(userId, 200);
    }

    public Results<Entitlement> getEntitlements(String userId, int expectedResponseCode) throws Exception {
        String entitlementGetUrl = entitlementUrl + "?userId=" + userId;
        String responseBody = restApiCall(HTTPMethod.GET, entitlementGetUrl, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Entitlement>>() {
        }, responseBody);
    }
}
