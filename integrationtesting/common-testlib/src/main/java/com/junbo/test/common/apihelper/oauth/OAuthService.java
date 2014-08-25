/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.common.apihelper.oauth;

import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;

/**
 * Created by weiyu_000 on 7/9/14.
 */
public interface OAuthService {

    String postAccessToken(GrantType grantType, ComponentType componentType) throws Exception;

    String postAccessToken(String clientId, String clientSecret, GrantType grantType, ComponentType componentType)
            throws Exception;

    String postAccessToken(String clientId, String clientSecret, GrantType grantType, ComponentType componentType,
                           int expectedResponseCode) throws Exception;

    String postUserAccessToken(String uid, String username, String pwd) throws Exception;

    String postUserAccessToken(String uid, String username, String pwd, int expectedResponseCode) throws Exception;

    String postEmailVerification(String uid, String country, String locale) throws Exception;

    String postEmailVerification(String uid, String country, String locale, int expectedResponseCode) throws Exception;

    String getCid() throws Exception;

    void authorizeLoginView(String cid) throws Exception;

    void authorizeRegister(String cid) throws Exception;

    void registerUser(String userName, String password, Country country, String cid) throws Exception;

    void registerUser(UserInfo userInfo, String cid) throws Exception;

    String postUserAccessToken(String username, String pwd) throws Exception;

    TokenInfo getTokenInfo(String accessToken) throws Exception;

    String getEmailVerifyLink(String cid) throws Exception;

    void accessEmailVerifyLink(String emailVerifyLink) throws Exception;

}
