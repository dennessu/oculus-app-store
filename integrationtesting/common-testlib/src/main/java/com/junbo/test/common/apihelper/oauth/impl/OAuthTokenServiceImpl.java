/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.oauth.impl;

import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthTokenService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.blueprint.Master;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by weiyu_000 on 7/9/14.
 */
public class OAuthTokenServiceImpl extends HttpClientBase implements OAuthTokenService {

    private static String oauthUrl = ConfigHelper.getSetting("defaultIdentityEndPointV1");

    private static OAuthTokenService instance;

    public static synchronized OAuthTokenService getInstance() {
        if (instance == null) {
            instance = new OAuthTokenServiceImpl();
        }
        return instance;
    }

    @Override
    protected FluentCaseInsensitiveStringsMap getHeader() {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        headers.add(Header.CONTENT_TYPE, "application/x-www-form-urlencoded");

        //for further header, we can set dynamic value from properties here
        return headers;
    }

    @Override
    public String postAccessToken(GrantType grantType, ComponentType componentType) throws Exception {
        return postAccessToken("service", "secret", grantType, componentType);
    }

    @Override
    public String postAccessToken(String clientId, String clientSecret, GrantType grantType,
                                  ComponentType componentType) throws Exception {
        return postAccessToken(clientId, clientSecret, grantType, componentType, 200);
    }

    @Override
    public String postAccessToken(String clientId, String clientSecret, GrantType grantType,
                                  ComponentType componentType, int expectedResponseCode) throws Exception {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);
        formParams.put("grant_type", grantType.toString());
        formParams.put("scope", componentType.toString() + ".service");

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "oauth2/token",
                convertFormtToRequestString(formParams), expectedResponseCode);

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        Master.getInstance().setIdentityAccessToken(accessTokenResponse.getAccessToken());

        return accessTokenResponse.getAccessToken();

    }

    @Override
    public String postUserAccessToken(String uid, String pwd) throws Exception {
        return postUserAccessToken(uid, pwd, 200);
    }

    @Override
    public String postUserAccessToken(String uid, String pwd, int expectedResponseCode) throws Exception {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", "client");
        formParams.put("client_secret", "secret");
        formParams.put("grant_type", GrantType.PASSWORD.toString());
        formParams.put("scope", "identity commerce catalog");
        formParams.put("password", pwd);
        formParams.put("username", Master.getInstance().getUser(uid).getUsername());

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "oauth2/token",
                convertFormtToRequestString(formParams), expectedResponseCode);

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        Master.getInstance().addUserAccessToken(uid, accessTokenResponse.getAccessToken());

        return accessTokenResponse.getAccessToken();
    }

    private String convertFormtToRequestString(Map<String, String> formParams) {
        String requestString = "";
        Set<String> keys = formParams.keySet();

        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            String value = formParams.get(key);
            requestString = requestString.concat(String.format("%s=%s&", key, value));
        }

        return requestString.substring(0, requestString.length() - 1);
    }

}
