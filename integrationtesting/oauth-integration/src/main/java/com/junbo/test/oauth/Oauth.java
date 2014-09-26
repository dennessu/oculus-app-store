/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.oauth.spec.model.ViewModel;
import com.junbo.test.common.*;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.identity.Identity;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.ws.rs.NotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dw
 */
public class Oauth {

    private Oauth() {

    }

    public static final String DefaultOauthEndpoint = ConfigHelper.getSetting("defaultOauthEndpoint");
    public static final String DefaultRedirectURI = ConfigHelper.getSetting("defaultRedirectURI");
    public static final String DefaultOauthSecondaryEndpoint = ConfigHelper.getSetting("secondaryDcEndpoint");

    public static final String DefaultAuthorizeURI = DefaultOauthEndpoint + "/oauth2/authorize";
    public static final String DefaultLogoutURI = DefaultOauthEndpoint + "/oauth2/end-session";
    public static final String DefaultResetPasswordURI = DefaultOauthEndpoint + "/oauth2/reset-password";
    public static final String DefaultTokenURI = DefaultOauthEndpoint + "/oauth2/token";
    public static final String DefaultTokenInfoURI = DefaultOauthEndpoint + "/oauth2/tokeninfo";

    public static final String DefaultClientId = ConfigHelper.getSetting("client_id");
    public static final String DefaultClientSecret = ConfigHelper.getSetting("client_secret");

    public static final String DefaultClientScopes = "identity";
    public static final String DefaultGrantType = "authorization_code";
    public static final String DefaultLoginScopes = "identity openid";
    public static final String DefaultRegisterEvent = "register";
    public static final String DefaultUserPwd = "1234qwerASDF";

    public static final String DefaultFNAccessToken = "access_token";
    public static final String DefaultFNCid = "cid";
    public static final String DefaultFNClientId = "client_id";
    public static final String DefaultFNClientSecret = "client_secret";
    public static final String DefaultFNCode = "code";
    public static final String DefaultFNDoB = "dob";
    public static final String DefaultFNEmail = "email";
    public static final String DefaultFNEvent = "event";
    public static final String DefaultFNFirstName = "first_name";
    public static final String DefaultFNGender = "gender";
    public static final String DefaultFNGrantType = "grant_type";
    public static final String DefaultFNIdToken = "id_token";
    public static final String DefaultFNLastName = "last_name";
    public static final String DefaultFNLocale = "locale";
    public static final String DefaultFNCountry = "country";
    public static final String DefaultFNLogin = "login";
    public static final String DefaultFNLoginState = "ls";
    //public static final String DefaultFNNickName = "nickname";
    public static final String DefaultFNPassword = "password";
    public static final String DefaultFNPin = "pin";
    public static final String DefaultFNRedirectURI = "redirect_uri";
    public static final String DefaultFNUserId = "userId";
    public static final String DefaultFNUserName = "username";


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
        return GetAccessToken(authCode, DefaultTokenURI);
    }

    public static String GetAccessToken(String authCode, String uri) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCode, authCode));
        nvps.add(new BasicNameValuePair(DefaultFNClientId, DefaultClientId));
        nvps.add(new BasicNameValuePair(DefaultFNClientSecret, DefaultClientSecret));
        nvps.add(new BasicNameValuePair(DefaultFNGrantType, DefaultGrantType));
        nvps.add(new BasicNameValuePair(DefaultFNRedirectURI, DefaultRedirectURI));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(uri, nvps, false);
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
        return GetTokenInfo(accessToken, DefaultTokenInfoURI);
    }

    public static TokenInfo GetTokenInfo(String accessToken, String uri) throws Exception {
        return HttpclientHelper.SimpleGet(uri + "?access_token=" + accessToken, TokenInfo.class);
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
            String tarHeader = "location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().toLowerCase().startsWith(tarHeader)) {
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
        return GetViewStateByCid(cid, DefaultAuthorizeURI);
    }

    public static String GetViewStateByCid(String cid, String uri) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(uri + "?cid=" + cid);
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
        return PostRegisterUser(cid, userName, email, true, false);
    }

    // pass in userName for validation purpose only
    public static String PostRegisterUser(String cid, String userName, String email,
                                          Boolean verifyEmail, Boolean doubleVerifyEmail) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, DefaultUserPwd));
        nvps.add(new BasicNameValuePair(DefaultFNEmail, email));
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
            if (verifyEmail) RunPostRegistrationWithEmailVerification(cid, doubleVerifyEmail);
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
            String tarHeader = "set-cookie";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().toLowerCase().startsWith(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNLoginState, ";");
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static void VerifyEmail(String cid, String uriEndPoint) throws Exception {
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
        emailLink = URLProtocolAuthorityReplace(emailLink, uriEndPoint);
        VerifyEmail(emailLink, false);
    }


    private static void RunPostRegistrationWithEmailVerification(String cid, Boolean doubleVerifyEmail)
            throws Exception {
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
        emailLink = URLProtocolAuthorityReplace(emailLink, DefaultOauthEndpoint);
        VerifyEmail(emailLink, false);
        if (doubleVerifyEmail) VerifyEmail(emailLink, doubleVerifyEmail);
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

    public static String UserLogin(String cid, String email, String password) throws Exception {
        return UserLogin(cid, email, password, DefaultAuthorizeURI);
    }

    public static String UserLogin(String cid, String email, String password, String uri) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNLogin, email));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, password == null ? DefaultUserPwd : password));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(uri, nvps, false);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate no error", true, viewModelResponse.getErrors().isEmpty());
            return viewModelResponse.getModel().get("location").toString();
        } finally {
            response.close();
        }
    }

    public static Map<String, String> GetLoginUser(String requestURI) throws Exception {
        return GetLoginUser(requestURI, DefaultOauthEndpoint);
    }

    public static Map<String, String> GetLoginUser(String requestURI, String uri) throws Exception {
        Map<String, String> results = new HashMap<>();
        requestURI = URLProtocolAuthorityReplace(requestURI, uri);
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(requestURI, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    results.put(DefaultFNAccessToken,
                            GetPropertyValueFromString(h.toString(), DefaultFNAccessToken, "&"));
                    results.put(DefaultFNIdToken,
                            GetPropertyValueFromString(h.toString(), DefaultFNIdToken, "&"));
                    return results;
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static String GetLoginAccessToken(String requestURI) throws Exception {
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(requestURI, false);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.toString().startsWith(tarHeader)) {
                    return h.toString().substring(h.toString().indexOf("access_token=") + "access_token=".length());
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
                    Validator.Validate("validate logout success", true, h.toString().contains("Location: http://localhost"));
                    return;
                }
            }
            throw new NotFoundException(
                    "Did not found expected response header: " + tarHeader + " in response");
        } finally {
            response.close();
        }
    }

    public static void VerifyEmail(String link, Boolean validateUsedToken) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("Accept", "application/json"));
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(link, nvps, true);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate view model", "emailVerify", viewModelResponse.getView());
            Validator.Validate("validate email verify result", validateUsedToken ? false : true,
                    viewModelResponse.getModel().get("verifyResult"));
            Validator.Validate("validate email verify errors", validateUsedToken ? false : true,
                    viewModelResponse.getErrors().isEmpty());
        } finally {
            response.close();
        }
    }

    public static String PostSendVerifyEmail(String userId, String piid, String uri) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNUserId, userId));
        nvps.add(new BasicNameValuePair(DefaultFNLocale, "en_US"));
        nvps.add(new BasicNameValuePair(DefaultFNCountry, "US"));
        nvps.add(new BasicNameValuePair("tml", piid));

        CloseableHttpResponse response = HttpclientHelper.SimplePost(uri + "/oauth2/verify-email", nvps, false);
        try {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
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

    public static List<String> GetResetPasswordLinks(String userName, String email, String locale) throws Exception {
        String uri = String.format(DefaultResetPasswordURI + "/test?username=%s&email=%s&locale=%s&country=%s",
                userName, email, locale == null ? "en_US" : locale, Country.DEFAULT.toString());
        CloseableHttpResponse response = HttpclientHelper.SimpleGet(uri, false);

        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return ObjectMapperProvider.instance().readValue(responseString, TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, String.class));
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

    public static String URLProtocolAuthorityReplace(String url1, String url2) throws Exception {
        url1 = url1.replace(new URL(url1).getProtocol(), new URL(url2).getProtocol());
        url1 = url1.replace(new URL(url1).getAuthority(), new URL(url2).getAuthority());
        return url1;
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
