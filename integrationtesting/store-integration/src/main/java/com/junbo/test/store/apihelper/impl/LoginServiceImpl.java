/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.store.apihelper.LoginService;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class LoginServiceImpl extends HttpClientBase implements LoginService {

    private static String loginUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1") + "storeapi/id/";

    private static LoginService instance;

    public static synchronized LoginService getInstance() {
        if (instance == null) {
            instance = new LoginServiceImpl();
        }
        return instance;
    }

    @Override
    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest) throws Exception {
        return CreateUser(createUserRequest, 200);
    }

    @Override
    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, loginUrl + "create", createUserRequest);
        if (expectedResponseCode == 200) {
            AuthTokenResponse authTokenResponse = new JsonMessageTranscoder().decode(new TypeReference<AuthTokenResponse>() {
            }, responseBody);
            Master.getInstance().addUserAccessToken(IdConverter.idToHexString(authTokenResponse.getUserId()), authTokenResponse.getAccessToken());
            return authTokenResponse;
        }
        return null;

    }
}
