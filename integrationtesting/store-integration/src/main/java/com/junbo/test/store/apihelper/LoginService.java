/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.common.error.*;
import com.junbo.common.error.Error;
import com.junbo.store.spec.model.login.*;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public interface LoginService {
    AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, int expectedResponseCode) throws Exception;

    com.junbo.common.error.Error CreateUserWithError(CreateUserRequest createUserRequest, int expectedResponseCode, String errorCode) throws Exception;

    UserNameCheckResponse CheckUserName(UserNameCheckRequest userNameCheckRequest) throws Exception;

    com.junbo.common.error.Error CheckUserNameWithError(UserNameCheckRequest userNameCheckRequest, int expectedResponseCode, String errorCode) throws Exception;

    EmailCheckResponse CheckEmail(EmailCheckRequest emailCheckRequest) throws Exception;

    com.junbo.common.error.Error CheckEmailWithError(EmailCheckRequest emailCheckRequest, int expectedResponseCode, String errorCode) throws Exception;

    AuthTokenResponse signIn(UserSignInRequest userSignInRequest) throws Exception;

    AuthTokenResponse signIn(UserSignInRequest userSignInRequest, int expectedResponseCode) throws Exception;

    Error signInWithError(UserSignInRequest userSignInRequest, int expectedResponseCode, String errorCode) throws Exception;

    UserCredentialRateResponse rateUserCredential(UserCredentialRateRequest userCredentialCheckRequest) throws Exception;

    com.junbo.common.error.Error rateUserCredentialWithError(UserCredentialRateRequest userCredentialRateRequest, int expectedResponseCode, String errorCode) throws Exception;

    AuthTokenResponse getToken(AuthTokenRequest request, int expectedResponseCode) throws Exception;

    Error getTokenWithError(AuthTokenRequest request, int expectedResponseCode, String errorCode) throws Exception;

    ConfirmEmailResponse confirmEmail(ConfirmEmailRequest request, int expectedResponseCode) throws Exception;

    Error confirmEmail(ConfirmEmailRequest request, int expectedResponseCode, String errorCode) throws Exception;
}
