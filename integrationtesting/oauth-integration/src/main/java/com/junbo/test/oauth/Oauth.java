/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.junbo.common.error.Error;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.oauth.spec.model.AccessTokenResponse;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.oauth.spec.model.ViewModel;
import com.junbo.test.common.*;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.identity.Identity;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.NotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

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

    public static final String DefaultClientIdExt = ConfigHelper.getSetting("client_id_external");
    //public static final String DefaultClientSecretExt = ConfigHelper.getSetting("client_secret_external");

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
    public static final String DefaultFNResponseType = "response_type";
    public static final String DefaultFNUserId = "userId";
    public static final String DefaultFNUserName = "username";
    public static final String DefaultFNUserEmail = "user_email";

    public static final String OculusInternalHeader = "oculus-internal";
    public static final String OculusInternalHeaderDefaultValue = String.valueOf(true);

    public static CloseableHttpResponse OauthPost(String requestURI, List<NameValuePair> nvps) throws Exception {
        return OauthPost(requestURI, nvps, true, false);
    }

    public static CloseableHttpResponse OauthPost(String requestURI, List<NameValuePair> nvps,
                                                  Boolean addInternalHeader, Boolean enableRedirect) throws Exception {
        HttpPost httpPost = new HttpPost(requestURI);
        if (addInternalHeader) httpPost.addHeader(OculusInternalHeader, OculusInternalHeaderDefaultValue);
        httpPost.setConfig(RequestConfig.custom()
                .setRedirectsEnabled(enableRedirect).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build());
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        return HttpclientHelper.Execute(httpPost);
    }

    public static CloseableHttpResponse OauthPut(String requestURI, List<NameValuePair> nvps) throws Exception {
        return OauthPut(requestURI, nvps, true, false);
    }

    public static CloseableHttpResponse OauthPut(String requestURI, List<NameValuePair> nvps,
                                                 Boolean addInternalHeader, Boolean enableRedirect) throws Exception {
        HttpPut httpPut = new HttpPut(requestURI);
        if (addInternalHeader) httpPut.addHeader(OculusInternalHeader, OculusInternalHeaderDefaultValue);
        httpPut.setConfig(RequestConfig.custom()
                .setRedirectsEnabled(enableRedirect).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build());
        httpPut.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
        return HttpclientHelper.Execute(httpPut);
    }

    public static CloseableHttpResponse OauthGet(String requestURI, List<NameValuePair> nvpHeaders) throws Exception {
        return OauthGet(requestURI, nvpHeaders, true, false);
    }

    public static CloseableHttpResponse OauthGet(String requestURI, List<NameValuePair> nvpHeaders,
                                                 Boolean addInternalHeader, Boolean enableRedirect) throws Exception {
        HttpGet httpGet = new HttpGet(requestURI);
        if (addInternalHeader) httpGet.addHeader(OculusInternalHeader, OculusInternalHeaderDefaultValue);
        httpGet.setConfig(RequestConfig.custom()
                .setRedirectsEnabled(enableRedirect).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build());
        if (nvpHeaders != null && !nvpHeaders.isEmpty()) {
            for (NameValuePair nvp : nvpHeaders) {
                httpGet.addHeader(nvp.getName(), nvp.getValue());
            }
        }
        return HttpclientHelper.Execute(httpGet);
    }

    public static void OauthDelete(String requestURI) throws Exception {
        OauthDelete(requestURI, true, false);
    }

    public static void OauthDelete(String requestURI, Boolean addInternalHeader, Boolean enableRedirect)
            throws Exception {
        HttpDelete httpDelete = new HttpDelete(requestURI);
        if (addInternalHeader) httpDelete.addHeader(OculusInternalHeader, OculusInternalHeaderDefaultValue);
        httpDelete.setConfig(RequestConfig.custom()
                .setRedirectsEnabled(enableRedirect).setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build());
        HttpclientHelper.Execute(httpDelete);
    }


    public static String GetRegistrationCid() throws Exception {
        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI
                + "?client_id="
                + DefaultClientId
                + "&response_type=code&scope=identity&redirect_uri="
                + DefaultRedirectURI, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
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
        return GetAccessToken(authCode, uri, DefaultClientId);
    }

    public static String GetAccessToken(String authCode, String uri, String clientId) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCode, authCode));
        nvps.add(new BasicNameValuePair(DefaultFNClientId, clientId));
        nvps.add(new BasicNameValuePair(DefaultFNClientSecret, DefaultClientSecret));
        nvps.add(new BasicNameValuePair(DefaultFNGrantType, DefaultGrantType));
        nvps.add(new BasicNameValuePair(DefaultFNRedirectURI, DefaultRedirectURI));

        CloseableHttpResponse response = OauthPost(uri, nvps);
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

    public static String PostAccessToken(String clientId, String scope) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNClientId, clientId));
        nvps.add(new BasicNameValuePair(DefaultFNClientSecret, DefaultClientSecret));
        nvps.add(new BasicNameValuePair(DefaultFNGrantType, "client_credentials"));
        nvps.add(new BasicNameValuePair("scope", scope));

        CloseableHttpResponse response = OauthPost(DefaultTokenURI, nvps);
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
        return JsonHelper.JsonDeserializer(
                new InputStreamReader(OauthGet(uri + "?access_token=" + accessToken, null).getEntity().getContent()),
                TokenInfo.class);
    }


    public static String SSO2GetAuthCode(String loginState) throws Exception {
        List<NameValuePair> nvpHeaders = new ArrayList<NameValuePair>();
        nvpHeaders.add(new BasicNameValuePair("Cookie", "ls=" + loginState));

        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI
                + "?client_id="
                + DefaultClientId
                + "&response_type=code&scope=identity&redirect_uri="
                + DefaultRedirectURI, nvpHeaders);
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

    public static CloseableHttpResponse GetViewStateByCid(String cid) throws Exception {
        return GetViewStateByCid(cid, DefaultAuthorizeURI);
    }

    public static CloseableHttpResponse GetViewStateByCid(String cid, String uri) throws Exception {
        return OauthGet(uri + "?cid=" + cid, null);
        /*
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            //System.out.print(responseString);
            return responseString;
        } finally {
            response.close();
        }
        */
    }

    public static CloseableHttpResponse PostViewRegisterByCid(String cid) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, DefaultRegisterEvent));

        return OauthPost(DefaultAuthorizeURI, nvps);
    }

    // pass in userName and email for validation purpose only
    public static void PostRegisterUser(
            String cid, String userName, String email, ViewModel emailVerifyRequiredViewModel)
            throws Exception {
        PostRegisterUser(cid, userName, email, null, true, false, emailVerifyRequiredViewModel);
    }

    public static void PostRegisterUser(String cid, String userName, String email)
            throws Exception {
        PostRegisterUser(cid, userName, email, null, true, false, null);
    }

    public static void PostRegisterUser(String cid, String userName, String email, String password, String pin) throws Exception {
        PostRegisterUser(cid, userName, email, null, true, false, null, pin, password);
    }

    public static void PostRegisterUser(String cid, String userName, String email, Error error)
            throws Exception {
        PostRegisterUser(cid, userName, email, error, false, false, null);
    }

    // pass in userName and email for validation purpose only
    public static void PostRegisterUser(
            String cid, String userName, String email, Error errors, Boolean verifyEmail, Boolean doubleVerifyEmail,
            ViewModel emailVerifyRequiredViewModel, String pin, String password) throws Exception{
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, password));
        nvps.add(new BasicNameValuePair(DefaultFNEmail, email));
        nvps.add(new BasicNameValuePair(DefaultFNFirstName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNLastName, RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair(DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(DefaultFNPin, pin));

        CloseableHttpResponse response = OauthPost(DefaultAuthorizeURI, nvps);
        try {
            ViewModel viewModel = JsonHelper.JsonDeserializer(new InputStreamReader(response.getEntity().getContent()),
                    ViewModel.class);
            if (errors == null) {
                Validator.Validate("validate no errors", 0, viewModel.getErrors().size());
            } else {
                Validator.Validate("validate errors",
                        errors.getDetails().size(),
                        viewModel.getErrors().get(0).getDetails() == null ? 0 : viewModel.getErrors().get(0).getDetails().size());
                Validator.Validate("validate error message",
                        errors.getMessage(),
                        viewModel.getErrors().get(0).getMessage());
                for (int i = 0; i < errors.getDetails().size(); i++) {
                    if (!StringUtils.isEmpty(errors.getCode())) {
                        Validator.Validate("Validate error code", errors.getCode(), viewModel.getErrors().get(0).getCode());
                    }
                    Validator.Validate("validate error field",
                            errors.getDetails().get(i).getField(),
                            viewModel.getErrors().get(0).getDetails().get(i).getField());
                    Validator.Validate("validate error reason",
                            errors.getDetails().get(i).getReason(),
                            viewModel.getErrors().get(0).getDetails().get(i).getReason());
                }
                return;
            }
            if (verifyEmail) {
                RunPostRegistrationWithEmailVerification(cid, doubleVerifyEmail, emailVerifyRequiredViewModel);
            }
        } finally {
            response.close();
        }
    }

    public static void PostRegisterUser(
            String cid, String userName, String email, Error errors, Boolean verifyEmail, Boolean doubleVerifyEmail,
            ViewModel emailVerifyRequiredViewModel) throws Exception {
        PostRegisterUser(cid, userName, email, errors, verifyEmail, doubleVerifyEmail, emailVerifyRequiredViewModel, RandomHelper.randomNumeric(4), DefaultUserPwd);
    }

    public static String GetAuthCodeAfterRegisterUser(String cid) throws Exception {
        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI + "?cid=" + cid, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
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
        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI + "?cid=" + cid, null);
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
        String emailLink = GetEmailLinkFlowAfterRegistration(cid);
        emailLink = URLProtocolAuthorityReplace(emailLink, uriEndPoint);
        VerifyEmail(emailLink, false);
    }


    private static void RunPostRegistrationWithEmailVerification(
            String cid, Boolean doubleVerifyEmail, ViewModel emailVerifyRequiredViewModel)
            throws Exception {
        String emailLink = GetEmailLinkFlowAfterRegistration(cid, emailVerifyRequiredViewModel);
        List<NameValuePair> nvps;
        CloseableHttpResponse response;

        emailLink = URLProtocolAuthorityReplace(emailLink, DefaultOauthEndpoint);
        VerifyEmail(emailLink, false);
        if (doubleVerifyEmail) VerifyEmail(emailLink, doubleVerifyEmail);
        // goto next
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        response = OauthPost(DefaultAuthorizeURI, nvps);
        response.close();
    }

    public static String GetEmailLinkFlowAfterRegistration(String cid) throws Exception {
        return GetEmailLinkFlowAfterRegistration(cid, null);
    }

    private static String GetEmailLinkFlowAfterRegistration(String cid, ViewModel emailVerifyRequiredViewModel)
            throws Exception {
        // get payment method view
        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI + "?cid=" + cid, null);
        response.close();
        // skip payment method view
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "skip"));
        response = OauthPost(DefaultAuthorizeURI, nvps);
        response.close();
        // goto next and get email verified
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        response = OauthPost(DefaultAuthorizeURI, nvps);
        ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
        response.close();
        if (emailVerifyRequiredViewModel != null && emailVerifyRequiredViewModel.getModel() != null) {
            Iterator<Map.Entry<String, Object>> it = emailVerifyRequiredViewModel.getModel().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> objectEntry = (Map.Entry<String, Object>) it.next();
                Object obj = viewModelResponse.getModel().get(objectEntry.getKey());
                assert obj.equals(objectEntry.getValue());
            }
        }
        return viewModelResponse.getModel().get("link").toString();
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
        CloseableHttpResponse response = OauthGet(DefaultAuthorizeURI
                + "?client_id="
                + DefaultClientId
                + "&response_type=token%20id_token&scope=openid%20identity&"
                + "redirect_uri="
                + DefaultRedirectURI
                + "&nonce=randomstring&locale=en_US&state=randomstring", null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
                    return GetPropertyValueFromString(h.toString(), DefaultFNCid, "&");
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static String UserLogin(String cid, String email, String password) throws Exception {
        return UserLogin(cid, email, password, null);
    }

    public static String UserLogin(String cid, String email, String password, Error error) throws Exception {
        return UserLogin(cid, email, password, DefaultAuthorizeURI, error);
    }

    public static String UserLogin(String cid, String email, String password, String uri, Error error)
            throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(DefaultFNLogin, email));
        nvps.add(new BasicNameValuePair(DefaultFNPassword, password == null ? DefaultUserPwd : password));

        CloseableHttpResponse response = OauthPost(uri, nvps);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            if (error == null) {
                Validator.Validate("validate no error", true, viewModelResponse.getErrors().isEmpty());
                return viewModelResponse.getModel().get("location").toString();
            } else {
                Validator.Validate("validate error message",
                        error.getMessage(), viewModelResponse.getErrors().get(0).getMessage());
                Validator.Validate("validate error code",
                        error.getCode(), viewModelResponse.getErrors().get(0).getCode());
                Validator.Validate("validate error detail field",
                        error.getDetails().get(0).getField(),
                        viewModelResponse.getErrors().get(0).getDetails().get(0).getField());
                Validator.Validate("validate error detail reason",
                        error.getDetails().get(0).getReason(),
                        viewModelResponse.getErrors().get(0).getDetails().get(0).getReason());
                return null;
            }
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
        CloseableHttpResponse response = OauthGet(requestURI, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
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
        CloseableHttpResponse response = OauthGet(requestURI, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
                    return h.toString().substring(h.toString().indexOf("access_token=") + "access_token=".length());
                }
            }
            throw new NotFoundException("Did not found expected response header: " + tarHeader);
        } finally {
            response.close();
        }
    }

    public static void Logout(String idToken) throws Exception {
        CloseableHttpResponse response = OauthGet(DefaultLogoutURI
                + "?post_logout_redirect_uri="
                + DefaultRedirectURI
                + "&id_token_hint=" + idToken, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
                    Validator.Validate("validate logout success", true,
                            h.toString().contains("Location: http://localhost"));
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
        List<NameValuePair> nvpHeaders = new ArrayList<NameValuePair>();
        nvpHeaders.add(new BasicNameValuePair("Accept", "application/json"));
        CloseableHttpResponse response = OauthGet(link, nvpHeaders);
        try {
            ViewModel viewModelResponse = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate view model", "emailVerify", viewModelResponse.getView());
            Validator.Validate("validate email verify result", validateUsedToken ? false : true,
                    viewModelResponse.getModel().get("verifyResult"));
            Validator.Validate("validate email verify errors", validateUsedToken ? false : true,
                    viewModelResponse.getErrors().isEmpty());
            if (validateUsedToken) {
                Validator.Validate("validate error field", "evc",
                        viewModelResponse.getErrors().get(0).getDetails().get(0).getField());
                Validator.Validate("validate error field", "Field value is invalid.",
                        viewModelResponse.getErrors().get(0).getDetails().get(0).getReason());
            }
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

        CloseableHttpResponse response = OauthPost(uri + "/oauth2/verify-email", nvps);
        try {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            response.close();
        }
    }

    public static String PostResetPassword(String email, String locale) throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(DefaultFNUserEmail, email));
        nvps.add(new BasicNameValuePair(DefaultFNLocale, locale == null ? "en_US" : locale));

        CloseableHttpResponse response = OauthPost(DefaultResetPasswordURI, nvps);
        try {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            response.close();
        }
    }

    public static List<String> GetResetPasswordLinks(String userName, String email, String locale, boolean isForbidden)
            throws Exception {
        List<NameValuePair> nvpHeaders = new ArrayList<NameValuePair>();
        nvpHeaders.add(new BasicNameValuePair("Authorization", Identity.httpAuthorizationHeader));
        String uri = String.format(DefaultResetPasswordURI + "/test?username=%s&user_email=%s&locale=%s&country=%s",
                userName, URLEncoder.encode(email, "UTF-8"), locale == null ? "en_US" : locale,
                Country.DEFAULT.toString());
        CloseableHttpResponse response = OauthGet(uri, nvpHeaders);

        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            if (isForbidden) {
                assert responseString.contains("Forbidden");
                assert responseString.contains("The access token does not have sufficient scope to make the " +
                        "request to ResetPasswordEndpoint.getResetPasswordLink");
                return null;
            }
            return ObjectMapperProvider.instance().readValue(responseString, TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, String.class));
        } finally {
            response.close();
        }
    }

    public static String GetResetPasswordCid(String resetPasswordLink) throws Exception {
        CloseableHttpResponse response = OauthGet(resetPasswordLink, null);
        try {
            String tarHeader = "Location";
            for (Header h : response.getAllHeaders()) {
                if (h.getName().equals(tarHeader)) {
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
        CloseableHttpResponse response = OauthGet(DefaultResetPasswordURI + "?cid=" + cid, null);
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

        CloseableHttpResponse response = OauthPost(DefaultResetPasswordURI, nvps);
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

    public static void validateViewModeResponse(CloseableHttpResponse response, String viewIdentifier)
            throws Exception {
        try {
            ViewModel viewModel = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate no errors in response", true, viewModel.getErrors().isEmpty());
            Validator.Validate("validate view identifier", viewIdentifier.replace("_", "-"), viewModel.getView());
        } finally {
            response.close();
        }
    }

    public static void validateViewModeResponse(CloseableHttpResponse response, String viewIdentifier, Error error)
            throws Exception {
        try {
            ViewModel viewModel = JsonHelper.JsonDeserializer(
                    new InputStreamReader(response.getEntity().getContent()), ViewModel.class);
            Validator.Validate("validate view identifier", viewIdentifier.replace("_", "-"), viewModel.getView());
            Validator.Validate("validate errors in response", false, viewModel.getErrors().isEmpty());
            Validator.Validate("validate error message", error.getMessage(), viewModel.getErrors().get(0).getMessage());
            Validator.Validate("validate error field", error.getDetails().get(0).getField(),
                    viewModel.getErrors().get(0).getDetails().get(0).getField());
            Validator.Validate("validate error reason", error.getDetails().get(0).getReason(),
                    viewModel.getErrors().get(0).getDetails().get(0).getReason());
        } finally {
            response.close();
        }
    }

    /**
     * view model type enum.
     */
    public enum ViewModelType {
        emailVerify,
        emailVerifyRequired,
        login,
        payment_method,
        redirect,
        register,
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
