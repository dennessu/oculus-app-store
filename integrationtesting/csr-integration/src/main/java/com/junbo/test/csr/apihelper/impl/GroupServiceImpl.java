/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrGroup;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.csr.apihelper.GroupService;

import java.util.List;

/**
 * Created by weiyu_000 on 11/27/14.
 */
public class GroupServiceImpl extends HttpClientBase implements GroupService {

    private static GroupService instance;

    public static synchronized GroupService getInstance() {
        if (instance == null) {
            instance = new GroupServiceImpl();
        }
        return instance;
    }

    GroupServiceImpl() {
        endPointUrlSuffix = "/csr-groups";
        componentType = ComponentType.CSR;
    }

    @Override
    public List<CsrGroup> getCsrGroups() throws Exception {
        return getCsrGroups(200);
    }

    @Override
    public List<CsrGroup> getCsrGroups(int expectedResponseCode) throws Exception {
       return getCsrGroups(null, null);
    }

    @Override
    public List<CsrGroup> getCsrGroups(String groupName, String uid) throws Exception {
        return getCsrGroups(groupName, uid, 200);
    }

    @Override
    public List<CsrGroup> getCsrGroups(String groupName, String uid, int expectedResponseCode) throws Exception {
        String url = getEndPointUrl();
        boolean isUniqueParam = true;
        if (groupName != null && !groupName.isEmpty()) {
            url = url.concat("?groupName=" + groupName);
            isUniqueParam = false;
        }
        if (uid != null && !uid.isEmpty()) {
            url = isUniqueParam ? url.concat("?userId=" + uid) : url.concat("&userId=" + uid);
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);

        if (expectedResponseCode == 200) {
            Results<CsrGroup> groups = new JsonMessageTranscoder().decode(
                    new TypeReference<Results<CsrGroup>>() {
                    }, responseBody);
            return groups.getItems();
        }

        return null;
    }
}
