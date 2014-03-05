/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AuthorizationCode
import com.junbo.oauth.spec.model.ResponseType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Javadoc.
 */
@CompileStatic
class GrantAuthorizationCode implements Action {

    private AuthorizationCodeRepository authorizationCodeRepository

    private Long defaultExpiration

    @Required
    void setAuthorizationCodeRepository(AuthorizationCodeRepository authorizationCodeRepository) {
        this.authorizationCodeRepository = authorizationCodeRepository
    }

    @Required
    void setDefaultExpiration(Long defaultExpiration) {
        this.defaultExpiration = defaultExpiration
    }

    @Override
    boolean execute(ServiceContext context) {
        def oauthInfo = ServiceContextUtil.getOAuthInfo(context)
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        if (oauthInfo.responseTypes.contains(ResponseType.CODE)) {
            def appClient = ServiceContextUtil.getAppClient(context)
            def loginState = ServiceContextUtil.getLoginState(context)

            String nonce = null
            if (OAuthInfoUtil.isOpenIdConnect(oauthInfo)) {
                nonce = parameterMap.getFirst(OAuthParameters.NONCE)
                if (nonce == null && OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
                    throw AppExceptions.INSTANCE.missingNonce().exception()
                }
            }

            AuthorizationCode authorizationCode = new AuthorizationCode(
                    clientId: appClient.clientId,
                    userId: loginState.userId,
                    scopes: oauthInfo.scopes,
                    nonce: nonce,
                    redirectUri: oauthInfo.redirectUri,
                    expiredBy: new Date(System.currentTimeMillis() + defaultExpiration * 1000),
                    lastAuthDate: loginState.lastAuthDate
            )

            authorizationCodeRepository.save(authorizationCode)
            ServiceContextUtil.setAuthorizationCode(context, authorizationCode)
        }

        return true
    }
}
