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
import com.junbo.oauth.db.repo.AppClientRepository
import com.junbo.oauth.spec.model.AppClient
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * AuthenticateClient
 */
@CompileStatic
class AuthenticateClient implements Action {
    private static final int TOKENS_LENGTH = 2

    private AppClientRepository appClientRepository

    @Required
    void setAppClientRepository(AppClientRepository appClientRepository) {
        this.appClientRepository = appClientRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def headerMap = contextWrapper.headerMap

        String clientId, clientSecret
        String authorization = headerMap.getFirst(OAuthParameters.AUTHORIZATION)

        if (StringUtils.hasText(authorization)) {
            String[] tokens = authorization.split(' ')
            if (tokens.length != TOKENS_LENGTH || tokens[0] == 'Basic') {
                throw AppExceptions.INSTANCE.invalidAuthorization().exception()
            }

            String decoded = Base64.decodeBase64(tokens[1])

            tokens = decoded.split(':')

            if (tokens.length != TOKENS_LENGTH) {
                throw AppExceptions.INSTANCE.invalidAuthorization().exception()
            }

            clientId = tokens[0]
            clientSecret = tokens[1]
        }

        if (clientId == null) {
            clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        }

        if (!StringUtils.hasText(clientId)) {
            throw AppExceptions.INSTANCE.missingClientId().exception()
        }

        AppClient appClient = appClientRepository.getAppClient(clientId)

        if (appClient == null) {
            throw AppExceptions.INSTANCE.invalidClientId(clientId).exception()
        }

        if (clientSecret == null) {
            clientSecret = parameterMap.getFirst(OAuthParameters.CLIENT_SECRET)
        }

        if (!StringUtils.hasText(clientSecret)) {
            throw AppExceptions.INSTANCE.missingClientSecret().exception()
        }

        if (!appClient.clientSecret == clientSecret) {
            throw AppExceptions.INSTANCE.invalidClientSecret(clientSecret).exception()
        }

        contextWrapper.appClient = appClient

        return Promise.pure(null)
    }
}
