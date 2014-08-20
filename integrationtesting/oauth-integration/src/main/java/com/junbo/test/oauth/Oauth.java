/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.common.id.UserId;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.oauth.spec.model.ViewModel;
import com.junbo.test.common.*;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.identity.Identity;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.NotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class Oauth {

    private Oauth() {

    }

    public static final String DefaultAuthorizeURI = ConfigHelper.getSetting("defaultAuthorizeURI");
    public static final String DefaultClientId = ConfigHelper.getSetting("client_id");
    public static final String DefaultClientScopes = "identity";
    public static final String DefaultClientSecret = ConfigHelper.getSetting("client_secret");
    public static final String DefaultGrantType = "authorization_code";
    public static final String DefaultRedirectURI = ConfigHelper.getSetting("defaultRedirectURI");
    public static final String DefaultRegisterEvent = "register";
    public static final String DefaultResetPasswordURI = ConfigHelper.getSetting("defaultResetPasswordURI");
    public static final String DefaultTokenURI = ConfigHelper.getSetting("defaultTokenURI");
    public static final String DefaultTokenInfoURI = ConfigHelper.getSetting("defaultTokenInfoURI");
    public static final String DefaultLogoutURI = ConfigHelper.getSetting("defaultLogoutURI");

    public static final String DefaultFNCode = "code";
    public static final String DefaultFNCid = "cid";
    public static final String DefaultFNClientId = "client_id";
    public static final String DefaultFNClientSecret = "client_secret";
    public static final String DefaultFNGrantType = "grant_type";
    public static final String DefaultFNIdToken = "id_token";
    public static final String DefaultFNLoginState = "ls";
    public static final String DefaultFNRedirectURI = "redirect_uri";
    public static final String DefaultFNEvent = "event";

    public static final String DefaultFNUserId = "userId";
    public static final String DefaultFNLocale = "locale";
    public static final String DefaultFNUserName = "username";
    public static final String DefaultFNPassword = "password";
    public static final String DefaultFNEmail = "email";
    public static final String DefaultFNNickName = "nickname";
    public static final String DefaultFNFirstName = "first_name";
    public static final String DefaultFNLastName = "last_name";
    public static final String DefaultFNLogin = "login";
    public static final String DefaultFNGender = "gender";
    public static final String DefaultFNDoB = "dob";
    public static final String DefaultFNPin = "pin";

    public static final String DefaultUserPwd = "1234qwerASDF";

    public static String GetRegistrationCid() throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI
                        + "?client_id="
                        + DefaultClientId
                        + "&response_type=code&scope=identity&redirect_uri="
                        + DefaultRedirectURI,
                false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
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
            String accessToken = accessTokenResponse.getAccessToken();
            Identity.SetHttpAuthorizationHeader(accessToken);
            return accessToken;
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
                        + "?client_id="
                        + DefaultClientId
                        + "&response_type=code&scope=identity&redirect_uri="
                        + DefaultRedirectURI,
                nvpHeaders,
                false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
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
    public static String PostRegisterUser(String cid, String userName, String email) throws Exception {
        return PostRegisterUser(cid, userName, email, true);
    }

    // pass in userName for validation purpose only
    public static String PostRegisterUser(String cid, String userName, String email, Boolean verifyEmail)
            throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, DefaultUserPwd));
        nvps.add(new BasicNameValuePair(DefaultFNEmail, email));
        nvps.add(new BasicNameValuePair(DefaultFNNickName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNFirstName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNLastName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(DefaultFNPin, RandomHelper.randomNumeric(4)));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.print(responseString);
            return responseString;
        } finally {
            response.close();
            if (verifyEmail) RunPostRegistrationWithEmailVerification(cid);
        }
    }

    public static String GetAuthCodeAfterRegisterUser(String cid) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI + "?cid=" + cid, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
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
                if (h.toString().startsWith(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNLoginState, ";");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    private static void RunPostRegistrationWithEmailVerification(String cid) throws Exception {
        // get payment method view
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI + "?cid=" + cid, false);
        response.close();
        // skip payment method view
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "skip"));
        response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        response.close();
        // goto next and get email verified
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
        response.close();
        String emailLink = viewModelResponse.getModel().get("link").toString();
        emailLink = DefaultAuthorizeURI.replace("/authorize", "") + "/verify-email?"
                + emailLink.split("/verify-email?")[1];
        VerifyEmail(emailLink);
        // goto next
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        response.close();
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

    public static String GetEmailVerificationLink(Long userId) throws Exception {
        String query = String.format("select payload from shard_%s.email_history where user_id=%s;",
                ShardIdHelper.getShardIdByUid(IdConverter.idToUrlString(UserId.class, userId)), userId);
        String result = PostgresqlHelper.QuerySingleRowSingleColumn(query, "email");
        for (String s : result.split(",")) {
            if (s.contains("link")) {
                return s.replace("\"link\":\"", "").replace("\"", "").replace("}", "");
            }
        }
        throw new Exception("link is not found in:\r\n" + result);
    }

    public static String GetLoginCid() throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultAuthorizeURI
                        + "?client_id="
                        + DefaultClientId
                        + "&response_type=token%20id_token&scope=openid%20identity&"
                        + "redirect_uri="
                        + DefaultRedirectURI
                        + "&nonce=randomstring&locale=en_US&state=randomstring",
                false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCid, "&");
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static String UserLogin(String cid, String userName, String password) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNLogin, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, password == null ? DefaultUserPwd : password));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultAuthorizeURI, nvps, false);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate no error", true, viewModelResponse.getErrors().isEmpty());
            return viewModelResponse.getModel().get("location").toString();
        } finally {
            response.close();
        }
    }

    public static String GetLoginUserIdToken(String requestURI) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(requestURI, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNIdToken, "&");
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static void Logout(String idToken) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultLogoutURI
                + "?post_logout_redirect_uri="
                + DefaultRedirectURI
                + "&id_token_hint=" + idToken, false);
        try {
            String tarHeader = "Location:";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    assertEquals("validate logout success", true, h.toString().contains("Location: http://localhost"));
                    return;
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static void VerifyEmail(String link) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(link, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    assertEquals("validate email verify success", true, h.toString().contains("email-verify-success"));
                    return;
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static String PostResetPassword(String userId, String userName, String locale) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNUserId, userId));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNLocale, locale == null ? "en_US" : locale));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultResetPasswordURI, nvps, false);
        try {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            response.close();
        }
    }

    public static String GetResetPasswordCid(String resetPasswordLink) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(resetPasswordLink, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCid, "&");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static void GetResetPasswordView(String cid) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(DefaultResetPasswordURI + "?cid=" + cid, false);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate view", "reset_password", viewModelResponse.getView());
            Validator.Validate("validate no error", true, viewModelResponse.getErrors().isEmpty());
        } finally {
            response.close();
        }
    }

    public static void PostResetPasswordWithNewPassword(String cid, String newPassword) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, newPassword));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(DefaultResetPasswordURI, nvps, false);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate view", "reset_password_result", viewModelResponse.getView());
            Validator.Validate("validate reset password result", "true",
                    viewModelResponse.getModel().get("reset_password_success"));
            Validator.Validate("validate no error", true, viewModelResponse.getErrors().isEmpty());
        } finally {
            response.close();
        }
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
