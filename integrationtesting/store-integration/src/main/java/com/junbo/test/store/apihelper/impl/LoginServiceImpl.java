/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.common.error.Error;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.browse.document.Tos;
import com.junbo.store.spec.model.login.*;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.store.apihelper.LoginService;
import com.junbo.test.store.apihelper.TestContext;
import com.junbo.test.store.utility.DataGenerator;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class LoginServiceImpl extends HttpClientBase implements LoginService {

    private static LoginService instance;

    public static synchronized LoginService getInstance() {
        if (instance == null) {
            instance = new LoginServiceImpl();
        }
        return instance;
    }

    private LoginServiceImpl() {
        endPointUrlSuffix = "/horizon-api/id";
    }

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope, List<String> headersToRemove) {
        FluentCaseInsensitiveStringsMap headers = super.getHeader(isServiceScope, headersToRemove);
        headers.put("X-ANDROID-ID", Collections.singletonList(DataGenerator.instance().generateAndroidId()));
        headers.put("Accept-Language", Collections.singletonList("en-US"));
        for (Map.Entry<String, String> entry : TestContext.getData().getHeaders().entrySet()) {
            headers.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }

        if (currentEndPointType.equals(Master.EndPointType.Secondary)) {
            headers.put("Cache-Control", Collections.singletonList("no-cache"));
        }
        //for further header, we can set dynamic value from properties here
        return headers;
    }

    @Override
    public Tos GetRegisterTos() throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/tos");
        Tos tos = new JsonMessageTranscoder().decode(new TypeReference<Tos>() {
        }, responseBody);
        return tos;
    }

    @Override
    public GetSupportedCountriesResponse getSupportedCountryCodes() throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/countries");
        GetSupportedCountriesResponse supportedCountries = new JsonMessageTranscoder().decode(new TypeReference<GetSupportedCountriesResponse>() {
        }, responseBody);

        return supportedCountries;
    }

    @Override
    public Tos getPrivacyPolicyTos(int expectedResponseCode) throws Exception {
        String url = getEndPointUrl() + "/privacy-policy";
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        if (expectedResponseCode == 200) {
            Tos response = new JsonMessageTranscoder().decode(new TypeReference<Tos>() {
            }, responseBody);
            return response;
        }
        return null;
    }

    @Override
    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/register", createUserRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            AuthTokenResponse authTokenResponse = new JsonMessageTranscoder().decode(new TypeReference<AuthTokenResponse>() {
            }, responseBody);
            String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
            Master.getInstance().addUserAccessToken(uid, authTokenResponse.getAccessToken());
            Master.getInstance().setCurrentUid(uid);
            return authTokenResponse;
        }
        return null;
    }

    @Override
    public com.junbo.common.error.Error CreateUserWithError(CreateUserRequest createUserRequest, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/register", createUserRequest, expectedResponseCode);
        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);
        assert error.getCode().equalsIgnoreCase(errorCode);

        return error;
    }

    @Override
    public UserNameCheckResponse CheckUserName(UserNameCheckRequest userNameCheckRequest) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/check-username?email=" + URLEncoder.encode(userNameCheckRequest.getEmail(), "UTF-8")
                + "&username=" + userNameCheckRequest.getUsername(),
                userNameCheckRequest);
        UserNameCheckResponse userNameCheckResponse = new JsonMessageTranscoder().decode(
                new TypeReference<UserNameCheckResponse>() {
                }, responseBody);
        return userNameCheckResponse;
    }

    @Override
    public com.junbo.common.error.Error CheckUserNameWithError(UserNameCheckRequest userNameCheckRequest, int expectedResponseCode,
                                                               String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/check-username?email="
                + (userNameCheckRequest.getEmail() == null ? "" : URLEncoder.encode(userNameCheckRequest.getEmail(), "UTF-8"))
                + "&username=" + (userNameCheckRequest.getUsername() == null ? "" : URLEncoder.encode(userNameCheckRequest.getUsername())),
                userNameCheckRequest, expectedResponseCode);
        Error error = new JsonMessageTranscoder().decode(
                new TypeReference<Error>() {
                }, responseBody);
        assert error.getCode().equalsIgnoreCase(errorCode);
        return error;
    }

    @Override
    public EmailCheckResponse CheckEmail(EmailCheckRequest emailCheckRequest) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/check-email?email=" + URLEncoder.encode(emailCheckRequest.getEmail(), "UTF-8"), emailCheckRequest);
        EmailCheckResponse emailCheckResponse = new JsonMessageTranscoder().decode(
                new TypeReference<EmailCheckResponse>() { }, responseBody);
        return emailCheckResponse;
    }

    @Override
    public Error CheckEmailWithError(EmailCheckRequest emailCheckRequest, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/check-email?email=" +
                (emailCheckRequest.getEmail() == null ? "" : URLEncoder.encode(emailCheckRequest.getEmail(), "UTF-8")), emailCheckRequest, expectedResponseCode);
        Error error = new JsonMessageTranscoder().decode(
                new TypeReference<Error>() {
                }, responseBody);
        assert error.getCode().equalsIgnoreCase(errorCode);
        return error;
    }

    @Override
    public AuthTokenResponse signIn(UserSignInRequest userSignInRequest) throws Exception {
        return signIn(userSignInRequest, 200);
    }

    @Override
    public AuthTokenResponse signIn(UserSignInRequest userSignInRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/log-in", userSignInRequest, expectedResponseCode);

        if (expectedResponseCode == 200) {
            AuthTokenResponse authTokenResponse = new JsonMessageTranscoder().decode(new TypeReference<AuthTokenResponse>() {
            }, responseBody);
            if (authTokenResponse.getUserId() != null) {
                String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
                Master.getInstance().addUserAccessToken(uid, authTokenResponse.getAccessToken());
                Master.getInstance().setCurrentUid(uid);
            }

            return authTokenResponse;
        }

        return null;
    }

    @Override
    public Error signInWithError(UserSignInRequest userSignInRequest, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/log-in", userSignInRequest, expectedResponseCode);

        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);

        assert error.getCode().equalsIgnoreCase(errorCode);
        return error;
    }

    @Override
    public UserCredentialRateResponse rateUserCredential(UserCredentialRateRequest userCredentialCheckRequest) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/rate-credential", userCredentialCheckRequest);

        UserCredentialRateResponse userCredentialRateResponse = new JsonMessageTranscoder().decode(
                new TypeReference<UserCredentialRateResponse>() {
                }, responseBody);
        return userCredentialRateResponse;
    }

    @Override
    public Error rateUserCredentialWithError(UserCredentialRateRequest userCredentialRateRequest, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/rate-credential", userCredentialRateRequest, expectedResponseCode);

        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);

        assert error.getCode().equalsIgnoreCase(errorCode);

        return error;
    }

    @Override
    public AuthTokenResponse getToken(AuthTokenRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/refresh-token", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            AuthTokenResponse authTokenResponse = new JsonMessageTranscoder().decode(new TypeReference<AuthTokenResponse>() {
            }, responseBody);
            String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
            Master.getInstance().addUserAccessToken(uid, authTokenResponse.getAccessToken());
            Master.getInstance().setCurrentUid(uid);
            return authTokenResponse;
        }
        return null;
    }

    @Override
    public Error getTokenWithError(AuthTokenRequest request, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/refresh-token", request, expectedResponseCode);

        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);
        assert error.getCode().equalsIgnoreCase(errorCode);
        return error;
    }

    @Override
    public ConfirmEmailResponse confirmEmail(ConfirmEmailRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/confirm-email", request, expectedResponseCode);

        if (expectedResponseCode == 200) {
            ConfirmEmailResponse response = new JsonMessageTranscoder().decode(new TypeReference<ConfirmEmailResponse>() {
            }, responseBody);
            return response;
        }
        return null;
    }

    @Override
    public Error confirmEmail(ConfirmEmailRequest request, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/confirm-email", request, expectedResponseCode);

        Error error = new JsonMessageTranscoder().decode(new TypeReference<Error>() {
        }, responseBody);
        assert error.getCode().equalsIgnoreCase(errorCode);
        return error;
    }

    private String appendQuery(String url, String name, Object val) throws UnsupportedEncodingException {

        if (val != null) {
            return url + "&" + name + "=" + URLEncoder.encode(val.toString(), "UTF-8");
        }
        return url;
    }
}
