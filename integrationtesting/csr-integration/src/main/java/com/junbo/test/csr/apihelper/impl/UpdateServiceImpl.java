/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper.impl;

import com.junbo.common.id.UserId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrUpdate;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.csr.apihelper.UpdateService;

import java.util.List;

/**
 * Created by weiyu_000 on 11/28/14.
 */
public class UpdateServiceImpl extends HttpClientBase implements UpdateService {

    private static UpdateService instance;

    public static synchronized UpdateService getInstance() {
        if (instance == null) {
            instance = new UpdateServiceImpl();
        }
        return instance;
    }

    UpdateServiceImpl() {
        endPointUrlSuffix = "/csr-updates";
        componentType = ComponentType.CSR;

    }

    @Override
    public CsrUpdate createUpdate(String uid, String content) throws Exception {
        return createUpdate(uid, content, 201);
    }

    @Override
    public CsrUpdate createUpdate(String uid, String content, int expectedResponseCode) throws Exception {
        CsrUpdate update = new CsrUpdate();
        update.setContent(content);
        update.setActive(true);
        update.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl(), update, expectedResponseCode);

        if (expectedResponseCode == 201) {
            CsrUpdate csrUpdate = new JsonMessageTranscoder().decode(
                    new TypeReference<CsrUpdate>() {
                    }, responseBody);
            return csrUpdate;
        }

        return null;
    }

    @Override
    public CsrUpdate putUpdate(String updateId, CsrUpdate update) throws Exception {
        return  putUpdate(updateId, update, 200);
    }

    @Override
    public CsrUpdate putUpdate(String updateId, CsrUpdate update, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/" + updateId, update, expectedResponseCode);

        if (expectedResponseCode == 200) {
            CsrUpdate csrUpdate = new JsonMessageTranscoder().decode(
                    new TypeReference<CsrUpdate>() {
                    }, responseBody);
            return csrUpdate;
        }

        return null;
    }

    @Override
    public CsrUpdate patchUpdate(String updateId, String uid, String content) throws Exception {
        return patchUpdate(updateId, uid, content, 201);
    }

    @Override
    public CsrUpdate patchUpdate(String updatedId, String uid, String content, int expectedResponseCode) throws Exception {
        CsrUpdate update = new CsrUpdate();
        update.setContent(content);
        update.setActive(true);
        update.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/" + updatedId, update, expectedResponseCode);

        if (expectedResponseCode == 201) {
            CsrUpdate csrUpdate = new JsonMessageTranscoder().decode(
                    new TypeReference<CsrUpdate>() {
                    }, responseBody);
            return csrUpdate;
        }

        return null;
    }

    @Override
    public CsrUpdate getUpdate(String updateId) throws Exception {
        return getUpdate(updateId, 200);
    }

    @Override
    public CsrUpdate getUpdate(String updateId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/" + updateId, expectedResponseCode);

        if (expectedResponseCode == 200) {
            CsrUpdate csrUpdate = new JsonMessageTranscoder().decode(
                    new TypeReference<CsrUpdate>() {
                    }, responseBody);
            return csrUpdate;
        }

        return null;
    }

    @Override
    public List<CsrUpdate> getUpdateList(boolean isActive) throws Exception {
        return getUpdateList(isActive, 200);
    }

    @Override
    public List<CsrUpdate> getUpdateList(boolean isActive, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "?active=" + String.valueOf(isActive), expectedResponseCode);

        if (expectedResponseCode == 200) {
            Results<CsrUpdate> csrUpdates = new JsonMessageTranscoder().decode(
                    new TypeReference<Results<CsrUpdate>>() {
                    }, responseBody);
            return csrUpdates.getItems();
        }

        return null;
    }

    @Override
    public boolean deleteUpdate(String updateId) throws Exception {
        return deleteUpdate(updateId, 200);
    }

    @Override
    public boolean deleteUpdate(String updateId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.DELETE, getEndPointUrl() + "/" + updateId, expectedResponseCode);

        if (expectedResponseCode == 200) {
           return true;
        }

        return false;
    }
}
