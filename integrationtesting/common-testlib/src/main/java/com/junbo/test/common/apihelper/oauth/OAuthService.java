/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.common.apihelper.oauth;

import com.junbo.test.common.Entities.enums.ComponentType;
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

    String postUserAccessToken(String uid, String pwd) throws Exception;

    String postUserAccessToken(String uid, String pwd, int expectedResponseCode) throws Exception;

    String postEmailVerification(String uid, String country, String locale) throws Exception;

    String postEmailVerification(String uid, String country, String locale, int expectedResponseCode) throws Exception;

}
