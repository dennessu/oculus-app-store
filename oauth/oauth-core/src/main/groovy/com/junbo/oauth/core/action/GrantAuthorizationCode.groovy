/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AuthorizationCode
import com.junbo.oauth.spec.model.ResponseType
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * GrantAuthorizationCode.
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
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def parameterMap = contextWrapper.parameterMap

        if (oauthInfo.responseTypes.contains(ResponseType.CODE)) {
            def client = contextWrapper.client
            def loginState = contextWrapper.loginState

            String nonce = null
            if (OAuthInfoUtil.isOpenIdConnect(oauthInfo)) {
                nonce = parameterMap.getFirst(OAuthParameters.NONCE)
                if (nonce == null && OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
                    throw AppExceptions.INSTANCE.missingNonce().exception()
                }
            }

            AuthorizationCode authorizationCode = new AuthorizationCode(
                    clientId: client.clientId,
                    userId: loginState.userId,
                    scopes: oauthInfo.scopes,
                    nonce: nonce,
                    redirectUri: oauthInfo.redirectUri,
                    expiredBy: new Date(System.currentTimeMillis() + defaultExpiration * 1000),
                    lastAuthDate: loginState.lastAuthDate
            )

            authorizationCodeRepository.save(authorizationCode)
            contextWrapper.authorizationCode = authorizationCode
        }

        return Promise.pure(null)
    }
}
