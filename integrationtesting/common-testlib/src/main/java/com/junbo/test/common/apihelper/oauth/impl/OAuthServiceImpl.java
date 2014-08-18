/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.oauth.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
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

    private static String oauthUrl = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/oauth2";
    private static String identityPiiUrl = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/personal-info";

    private static OAuthService instance;

    private boolean needAuthHeader;
    private boolean needOverrideRequestEntity;

    public static synchronized OAuthService getInstance() {
        if (instance == null) {
            instance = new OAuthServiceImpl();
        }
        return instance;
    }

    private OAuthServiceImpl() {
        componentType = ComponentType.IDENTITY;
        contentType = "application/x-www-form-urlencoded";
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        String uid = Master.getInstance().getCurrentUid();
        if (needOverrideRequestEntity) {
            headers.add(Header.CONTENT_TYPE, contentType);
        }
        if (needAuthHeader) {
            if (isServiceScope) {
                headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getServiceAccessToken(componentType));
            } else if (uid != null && Master.getInstance().getUserAccessToken(uid) != null) {
                headers.add(Header.AUTHORIZATION, "Bearer " + Master.getInstance().getUserAccessToken(uid));
            }
        }

        //for further header, we can set dynamic value from properties here
        return headers;
    }

    @Override
    public String postAccessToken(GrantType grantType, ComponentType componentType) throws Exception {
        if (ConfigHelper.getSetting("client_secret") != null) {
            return postAccessToken("service", ConfigHelper.getSetting("client_secret"), grantType, componentType);
        }
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
        needAuthHeader = false;
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
            case EMAILADMIN:
                formParams.put("scope", "email.admin");
                break;
            case DRM:
                formParams.put("scope", "drm");
                clientId = "client";
                break;
            default:
                formParams.put("scope", componentType.toString() + ".service");
        }
        formParams.put("client_id", "service");
        formParams.put("client_secret", clientSecret);
        formParams.put("grant_type", grantType.toString());


        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "/token",
                convertFormatToRequestString(formParams), expectedResponseCode);

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        Master.getInstance().addServiceAccessToken(componentType, accessTokenResponse.getAccessToken());

        return accessTokenResponse.getAccessToken();
    }

    @Override
    public String postUserAccessToken(String uid, String username, String pwd) throws Exception {
        return postUserAccessToken(uid, username, pwd, 200);
    }

    @Override
    public String postUserAccessToken(String uid, String username, String pwd, int expectedResponseCode) throws Exception {
        needAuthHeader = false;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", ConfigHelper.getSetting("client_id"));
        formParams.put("client_secret", ConfigHelper.getSetting("secret"));
        formParams.put("grant_type", GrantType.PASSWORD.toString());
        formParams.put("scope", "identity commerce catalog identity.pii catalog.developer");
        formParams.put("password", pwd);
        formParams.put("username", username);

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "/token",
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
        needAuthHeader = true;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("userId", uid);
        formParams.put("country", country);
        formParams.put("locale", locale);

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "/verify-email",
                convertFormatToRequestString(formParams), expectedResponseCode, true);

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
        needOverrideRequestEntity = true;
        return requestString.substring(0, requestString.length() - 1);
    }

    @Override
    public String getCid() throws Exception {
        needAuthHeader = false;
        needOverrideRequestEntity = false;
        String url = String.format("/authorize?client_id=%s&response_type=code&scope=identity&redirect_uri=%s",
                ConfigHelper.getSetting("client_id"), ConfigHelper.getSetting("oauth.redirect.uri"));

        String responseBody = restApiCall(HTTPMethod.GET, oauthUrl + url);

        return responseBody.substring(responseBody.indexOf('=') + 1);
    }

    @Override
    public void authorizeLoginView(String cid) throws Exception {
        needAuthHeader = false;
        needOverrideRequestEntity = false;
        String url = String.format("/authorize?cid=%s", cid);
        String responseBody = restApiCall(HTTPMethod.GET, oauthUrl + url);
    }

    @Override
    public void authorizeRegister(String cid) throws Exception {
        needAuthHeader = true;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("cid", cid);
        formParams.put("event", "register");

        restApiCall(HTTPMethod.POST, oauthUrl + "/authorize",
                convertFormatToRequestString(formParams));
    }

    @Override
    public void registerUser(String userName, String password, Country country, String cid) throws Exception {
        needAuthHeader = true;

        Map<String, String> formParams = new HashMap<>();
        formParams.put("cid", cid);
        formParams.put("event", "next");
        formParams.put("nickname", "test");
        formParams.put("first_name", "test");
        formParams.put("last_name", "test");
        formParams.put("gender", "male");
        formParams.put("dob", "1980-01-01");
        formParams.put("username", userName);
        formParams.put("password", "Test1234");
        formParams.put("email", userName + "@gmail.com");
        formParams.put("pin", "1234");
        formParams.put("country", country.toString());

        restApiCall(HTTPMethod.POST, oauthUrl + "/authorize",
                convertFormatToRequestString(formParams));

    }

    @Override
    public void registerUser(UserInfo userInfo, String cid) throws Exception {
        needAuthHeader = true;

        Map<String, String> formParams = new HashMap<>();
        formParams.put("cid", cid);
        formParams.put("event", "next");
        formParams.put("nickname", userInfo.getNickName());
        formParams.put("first_name", userInfo.getFirstName());
        formParams.put("last_name", userInfo.getLastName());
        formParams.put("gender", userInfo.getGender().toString());
        formParams.put("dob", userInfo.getDob());
        formParams.put("username", userInfo.getUserName());
        formParams.put("password", userInfo.getPassword());
        formParams.put("email", userInfo.getEmails().get(0));
        formParams.put("pin", userInfo.getPin());
        formParams.put("country", userInfo.getCountry().toString());

        restApiCall(HTTPMethod.POST, oauthUrl + "/authorize",
                convertFormatToRequestString(formParams));
    }

    @Override
    public String postUserAccessToken(String username, String pwd) throws Exception {
        needAuthHeader = false;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", ConfigHelper.getSetting("client_id"));
        formParams.put("client_secret", ConfigHelper.getSetting("client_secret"));
        formParams.put("grant_type", GrantType.PASSWORD.toString());
        formParams.put("scope", "identity commerce catalog identity.pii catalog.developer");
        formParams.put("password", pwd);
        formParams.put("username", username);

        String responseBody = restApiCall(HTTPMethod.POST, oauthUrl + "/token",
                convertFormatToRequestString(formParams));

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        return accessTokenResponse.getAccessToken();
    }

    @Override
    public TokenInfo getTokenInfo(String accessToken) throws Exception {
        needAuthHeader = false;
        needOverrideRequestEntity = false;
        String url = String.format("/tokeninfo?access_token=%s", accessToken);
        String responseBody = restApiCall(HTTPMethod.GET, oauthUrl + url);

        TokenInfo tokenInfo = new JsonMessageTranscoder().decode(new TypeReference<TokenInfo>() {
        },
                responseBody);

        return tokenInfo;
    }

}
