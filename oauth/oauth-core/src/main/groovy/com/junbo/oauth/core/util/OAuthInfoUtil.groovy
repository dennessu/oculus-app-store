/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.oauth.spec.model.GrantType
import com.junbo.oauth.spec.model.OAuthInfo
import com.junbo.oauth.spec.model.ResponseType
import groovy.transform.CompileStatic

/**
 * OAuthInfoUtil.
 */
@CompileStatic
class OAuthInfoUtil {
    public static final String OPEN_ID_SCOPE = 'openid'
    public static final String OFFLINE_SCOPE = 'offline'

    static boolean isOpenIdConnect(OAuthInfo oauthInfo) {
        return oauthInfo.scopes != null && oauthInfo.scopes.contains(OPEN_ID_SCOPE)
    }

    static boolean isImplicitFlow(OAuthInfo oauthInfo) {
        return !(oauthInfo.responseTypes != null && oauthInfo.responseTypes.size() == 1
                && oauthInfo.responseTypes.contains(ResponseType.CODE))
    }

    static boolean isIdTokenNeeded(OAuthInfo oAuthInfo) {
        if (!isOpenIdConnect(oAuthInfo)) {
            return false
        }

        if (oAuthInfo.responseTypes != null && oAuthInfo.responseTypes.contains(ResponseType.ID_TOKEN)) {
            return true
        }

        if (GrantType.AUTHORIZATION_CODE == oAuthInfo.grantType) {
            return true
        }

        return false
    }
}
