/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.commerce.apiHelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.commerce.apiHelper.OauthService;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.apihelper.Header;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by weiyu_000 on 8/14/14.
 */
public class OauthServiceImpl extends HttpClientBase implements OauthService {
    private final String oauthServerURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/oauth2";

    private static OauthService instance;

    private boolean needHeader;

    public static synchronized OauthService instance() {
        if (instance == null) {
            instance = new OauthServiceImpl();
        }
        return instance;
    }

    private OauthServiceImpl() {
        contentType = "application/x-www-form-urlencoded";
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope) {
        FluentCaseInsensitiveStringsMap headers = new FluentCaseInsensitiveStringsMap();
        if (needHeader) {
            headers.add(Header.CONTENT_TYPE, contentType);
        }

        return headers;
    }


    @Override
    public String getCid() throws Exception {
        needHeader = false;
        String url = String.format("/authorize?client_id=client&response_type=code&scope=identity&redirect_uri=http://localhost");
        String responseBody = restApiCall(HTTPMethod.GET, oauthServerURL + url);

        return responseBody.substring(responseBody.indexOf('=') + 1);
    }

    @Override
    public void authorizeLoginView(String cid) throws Exception {
        needHeader = false;
        String url = String.format("/authorize?cid=%s", cid);
        String responseBody = restApiCall(HTTPMethod.GET, oauthServerURL + url);
    }

    @Override
    public void authorizeRegister(String cid) throws Exception {
        needHeader = true;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("cid", cid);
        formParams.put("event", "register");

        restApiCall(HTTPMethod.POST, oauthServerURL + "/authorize",
                convertFormatToRequestString(formParams));
    }

    @Override
    public void registerUser(String userName, String password, Country country, String cid) throws Exception {
        needHeader = true;

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

        restApiCall(HTTPMethod.POST, oauthServerURL + "/authorize",
                convertFormatToRequestString(formParams));

    }

    @Override
    public String postUserAccessToken(String username, String pwd) throws Exception {
        needHeader = true;
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", "client");
        formParams.put("client_secret", ConfigHelper.getSetting("TestClient"));
        formParams.put("grant_type", GrantType.PASSWORD.toString());
        formParams.put("scope", "identity commerce catalog identity.pii catalog.developer");
        formParams.put("password", pwd);
        formParams.put("username", username);

        String responseBody = restApiCall(HTTPMethod.POST, oauthServerURL + "/token",
                convertFormatToRequestString(formParams));

        AccessTokenResponse accessTokenResponse = new JsonMessageTranscoder().decode(
                new TypeReference<AccessTokenResponse>() {
                }, responseBody
        );

        return accessTokenResponse.getAccessToken();
    }

    @Override
    public TokenInfo getTokenInfo(String accessToken) throws Exception {
        needHeader = false;
        String url = String.format("/tokeninfo?access_token=%s", accessToken);
        String responseBody = restApiCall(HTTPMethod.GET, oauthServerURL + url);

        TokenInfo tokenInfo = new JsonMessageTranscoder().decode(new TypeReference<TokenInfo>() {
        },
                responseBody);

        return tokenInfo;
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
