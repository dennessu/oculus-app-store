/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.GsonHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.identity.Identity;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.Header;
import org.apache.http.NameValuePair;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.NotFoundException;

/**
 * @author dw
 */
public class Oauth {

    private Oauth() {

    }

    public static final String DefaultAuthCodeEvent = "login";
    public static final String DefaultAuthorizeURI = "http://localhost:8082/oauth2/authorize";
    public static final String DefaultClientId = "client";
    public static final String DefaultClientScopes = "identity";
    public static final String DefaultClientSecret = "secret";
    public static final String DefaultGrantType = "authorization_code";
    public static final String DefaultRedirectURI = "http://localhost";
    public static final String DefaultTokenURI = "http://localhost:8082/oauth2/token";
    public static final String DefaultTokeninfoURI = "http://localhost:8082/oauth2/tokeninfo";

    public static final String DefaultFNCode = "code";
    public static final String DefaultFNCid = "cid";
    public static final String DefaultFNClientId = "client_id";
    public static final String DefaultFNClientSecret = "client_secret";
    public static final String DefaultFNGrantType = "grant_type";
    public static final String DefaultFNRedirectURI = "redirect_uri";
    public static final String DefaultFNEvent = "event";
    public static final String DefaultFNUserName = "username";
    public static final String DefaultFNPassword = "password";

    public static String GetLoginCid() throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(
                DefaultAuthorizeURI + "?client_id=client&response_type=code&scope=identity&redirect_uri=http://localhost",
                false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    return (h.toString().split("="))[1].split("&")[0];
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static String GetAuthCode(String cid, String userName) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, Identity.DefaultUserPwd));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, DefaultAuthCodeEvent));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    CloseableHttpResponse response2 = HttpclientHelper.SimpleGet(h.toString().replace("Location:", "").trim(), false);
                    try {
                        for (Header h2 : response2.getAllHeaders()) {
                            if (h2.toString().contains(tarHeader)) {
                                return h2.toString().split("&")[1].replace("code=", "").trim();
                            }
                        }
                        throw new NotFoundException("Did not found expected response header: " + tarHeader + " in response2");
                    } finally {
                        response2.close();
                    }
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static String GetAccessToken(String authCode) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCode, authCode));
        nvps.add(new BasicNameValuePair(DefaultFNClientId, DefaultClientId));
        nvps.add(new BasicNameValuePair(DefaultFNClientSecret, DefaultClientSecret));
        nvps.add(new BasicNameValuePair(DefaultFNGrantType, DefaultGrantType));
        nvps.add(new BasicNameValuePair(DefaultFNRedirectURI, DefaultRedirectURI));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultTokenURI, nvps, false);
        try {
            AccessTokenResponse accessTokenResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), AccessTokenResponse.class);
            return accessTokenResponse.getAccessToken();
        } finally {
            response.close();
        }
    }

    public static TokenInfo GetTokenInfo(String accessToken) throws Exception {
        return HttpclientHelper.SimpleGet(DefaultTokeninfoURI + "?access_token=" + accessToken, TokenInfo.class);
    }
}
