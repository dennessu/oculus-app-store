/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.NotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class Oauth {

    private Oauth() {

    }

    public static final String DefaultAuthorizeURI = ConfigHelper.getSetting("defaultAuthorizeURI");
    public static final String DefaultClientId = "client";
    public static final String DefaultClientScopes = "identity";
    public static final String DefaultClientSecret = "secret";
    public static final String DefaultGrantType = "authorization_code";
    public static final String DefaultRedirectURI = ConfigHelper.getSetting("defaultRedirectURI");
    public static final String DefaultRegisterEvent = "register";
    public static final String DefaultTokenURI = ConfigHelper.getSetting("defaultTokenURI");
    public static final String DefaultTokenInfoURI = ConfigHelper.getSetting("defaultTokenInfoURI");

    public static final String DefaultFNCode = "code";
    public static final String DefaultFNCid = "cid";
    public static final String DefaultFNClientId = "client_id";
    public static final String DefaultFNClientSecret = "client_secret";
    public static final String DefaultFNGrantType = "grant_type";
    public static final String DefaultFNLoginState = "ls";
    public static final String DefaultFNRedirectURI = "redirect_uri";
    public static final String DefaultFNEvent = "event";

    // identity user related
    public static final String DefaultFNUserName = "username";
    public static final String DefaultFNPassword = "password";
    public static final String DefaultFNEmail = "email";
    public static final String DefaultFNNickName = "nickname";
    public static final String DefaultFNFirstName = "first_name";
    public static final String DefaultFNLastName = "last_name";
    public static final String DefaultFNGender = "gender";
    public static final String DefaultFNDoB = "dob";

    public static final String DefaultUserPwd = "1234qwerASDF";
    public static final String DefaultUserEmail = "leoltd@163.com";

    public static String GetLoginCid() throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(
                DefaultAuthorizeURI
                        + "?client_id=client&response_type=code&scope=identity&redirect_uri=http://localhost",
                false
        );
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCid, "&");
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
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
        return HttpclientHelper.SimpleGet(DefaultTokenInfoURI + "?access_token=" + accessToken, TokenInfo.class);
    }

    public static String SSO2GetAuthCode(String loginState) throws Exception {
        List<NameValuePair> nvpHeaders = new ArrayList<NameValuePair>();
        nvpHeaders.add(new BasicNameValuePair("Cookie", "ls=" + loginState));

        CloseableHttpResponse response = HttpclientHelper.SimpleGet(
                DefaultAuthorizeURI
                        + "?client_id=client&response_type=code&scope=identity&redirect_uri=http://localhost",
                nvpHeaders, false
        );
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCode, "&");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static String GetViewStateByCid(String cid) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI + "?cid=" + cid);
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.print(responseString);
            return responseString;
        } finally {
            response.close();
        }
    }

    public static String PostViewRegisterByCid(String cid) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, DefaultRegisterEvent));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.print(responseString);
            return responseString;
        } finally {
            response.close();
        }
    }

    // pass in userName for validation purpose only
    public static String PostRegisterUser(String cid, String userName) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, DefaultUserPwd));
        nvps.add(new BasicNameValuePair(DefaultFNEmail, DefaultUserEmail));
        nvps.add(new BasicNameValuePair(DefaultFNNickName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNFirstName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNLastName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(DefaultFNDoB, "1980-01-01"));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.print(responseString);
            return responseString;
        } finally {
            response.close();
        }
    }

    public static String GetAuthCodeAfterRegisterUser(String cid) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI + "?cid=" + cid, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCode, "&");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static String GetLoginStateAfterRegisterUser(String cid) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI + "?cid=" + cid, false);
        try {
            String tarHeader = "Set-Cookie";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().contains(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNLoginState, ";");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    private static String GetPropertyValueFromString(String input, String property, String splitor) throws Exception {
        String[] results = input.split(splitor);
        for (String s : results) {
            if (s.contains(property)) {
                return s.split("=")[1].trim();
            }
        }
        throw new NotFoundException("Did not found expected property: " + property + " in " + input);
    }

    // ****** start API sample logging ******
    public static final String MessageGetLoginCid
            = "[Include In Sample][1] Description: Get_Login_Cid";
    public static final String MessageGetViewState
            = "[Include In Sample][1] Description: Get_ViewState_By_Cid";
    public static final String MessagePostViewRegister
            = "[Include In Sample][1] Description: Post_ViewRegister_By_Cid";
    public static final String MessagePostRegisterUser
            = "[Include In Sample][1] Description: Post_RegisterUser_By_Cid";
    public static final String MessageGetAuthCodeByCidAfterRegisterUser
            = "[Include In Sample][1] Description: Get_AuthCode_By_Cid_After_RegisterUser";
    public static final String MessageGetLoginStateByCidAfterRegisterUser
            = "[Include In Sample][1] Description: Get_LoginState_By_Cid_After_RegisterUser";
    public static final String MessageGetAuthCodeByLoginState
            = "[Include In Sample][1] Description: Get_AuthCode_By_LoginState";
    public static final String MessageGetAccessTokenByAuthCode
            = "[Include In Sample][1] Description: Get_Access_Token_By_AuthCode";
    public static final String MessageGetTokenInfoByAccessToken
            = "[Include In Sample][1] Description: Get_Token_Info_Access_Token";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }
}
