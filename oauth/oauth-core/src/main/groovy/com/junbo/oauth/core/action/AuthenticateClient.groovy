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
import com.junbo.oauth.core.util.AuthorizationHeaderUtil
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * AuthenticateClient
 */
@CompileStatic
class AuthenticateClient implements Action {
    private ClientRepository clientRepository

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def headerMap = contextWrapper.headerMap

        String clientId, clientSecret
        String authorization = headerMap.getFirst(OAuthParameters.AUTHORIZATION)

        if (StringUtils.hasText(authorization)) {
            def clientCredential = AuthorizationHeaderUtil.extractClientCredential(authorization)

            clientId = clientCredential.clientId
            clientSecret = clientCredential.clientSecret
        }

        if (clientId == null) {
            clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        }

        if (!StringUtils.hasText(clientId)) {
            throw AppExceptions.INSTANCE.missingClientId().exception()
        }

        Client appClient = clientRepository.getClient(clientId)

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

        contextWrapper.client = appClient

        return Promise.pure(null)
    }
}
