/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.def.SearchType;
import com.junbo.csr.spec.model.SearchForm;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.csr.apihelper.SearchService;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.*;

/**
 * Created by weiyu_000 on 11/26/14.
 */
public class SearchServiceImpl extends HttpClientBase implements SearchService {

    private static SearchService instance;

    public static synchronized SearchService getInstance() {
        if (instance == null) {
            instance = new SearchServiceImpl();
        }
        return instance;
    }

    SearchServiceImpl() {
        endPointUrlSuffix = "/csr-action";
        componentType = ComponentType.CSR;

    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.OCULUS_INTERNAL, String.valueOf(true));
        String uid = Master.getInstance().getCurrentUid();
        headers.add(Header.CONTENT_TYPE, contentType);
        if (uid != null && Master.getInstance().getUserAccessToken(uid) != null) {
            headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getUserAccessToken(uid));
        }

        return headers;
    }

    @Override
    public List<String> searchUsers(SearchType type, String value, int expectedResponseCode) throws Exception {
        SearchForm searchForm = new SearchForm();
        searchForm.setType(type);
        searchForm.setValue(value);
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/search", searchForm, expectedResponseCode);

        List<String> uids = new ArrayList<>();
        if (expectedResponseCode == 200) {
            Results<User> users = new JsonMessageTranscoder().decode(
                    new TypeReference<Results<User>>() {
                    }, responseBody);

            for (User user : users.getItems()) {
                String uid = IdConverter.idToHexString(user.getId());
                Master.getInstance().addUser(uid, user);
                uids.add(uid);
            }
            return uids;
        }
        return null;
    }

    @Override
    public List<String> searchUsers(SearchType type, String value) throws Exception {
        return searchUsers(type, value, 200);
    }

}
