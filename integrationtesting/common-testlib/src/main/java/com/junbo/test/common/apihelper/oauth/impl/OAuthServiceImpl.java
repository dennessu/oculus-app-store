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
import com.junbo.test.common.apihelper.oauth.OAuthService;
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
public class OAuthServiceImpl extends HttpClientBase implements OAuthService {

    private static String oauthUrl = ConfigHelper.getSetting("defaultIdentityEndPointV1");

    private static OAuthService instance;

    public static synchronized OAuthService getInstance() {
        if (instance == null) {
            instance = new OAuthServiceImpl();
        }
        return instance;
    }

    private OAuthServiceImpl() {
        componentType = ComponentType.IDENTITY;
    }

    @Override
    protected FluentCaseInsensitiveStringsMap getHeader(boolean isRoleAPI) {
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
        switch (componentType) {
            case ORDER:
                formParams.put("scope", "identity.service payment.service order.service");
                break;
            case BILLING:
                formParams.put("scope", "identity.service payment.service billing.service");
                break;
            case CATALOGADMIN:
                formParams.put("scope", "catalog.admin");
                break;
            default:
                formParams.put("scope", componentType.toString() + ".service");
        }
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);
        formParams.put("grant_type", grantType.toString());


        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "oauth2/token",
                convertFormatToRequestString(formParams), expectedResponseCode);

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        Master.getInstance().addServiceAccessToken(componentType, accessTokenResponse.getAccessToken());

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
        formParams.put("scope", "identity commerce catalog identity.pii");
        formParams.put("password", pwd);
        formParams.put("username", Master.getInstance().getUser(uid).getUsername());

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "oauth2/token",
                convertFormatToRequestString(formParams), expectedResponseCode);

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        Master.getInstance().addUserAccessToken(uid, accessTokenResponse.getAccessToken());

        return accessTokenResponse.getAccessToken();
    }

    @Override
    public String postEmailVerification(String uid, String country, String locale) throws Exception {
        return postEmailVerification(uid, country, locale);
    }

    @Override
    public String postEmailVerification(String uid, String country, String locale, int expectedResponseCode) throws Exception {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("userId", uid);
        formParams.put("country", country);
        formParams.put("locale", locale);

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "oauth2/verify-email",
                convertFormatToRequestString(formParams), expectedResponseCode);

        return responseBody;
    }

    private String convertFormatToRequestString(Map<String, String> formParams) {
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
