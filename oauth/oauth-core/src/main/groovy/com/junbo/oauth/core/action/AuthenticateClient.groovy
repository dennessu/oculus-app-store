/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.AuthorizationHeaderUtil
import com.junbo.oauth.db.repo.ClientRepository
import com.junbo.oauth.spec.model.Client
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.StringUtils

/**
 * The AuthenticateClient action.
 * This action will authenticate the client with the client id and client secret.
 * If either of them failed validation, exception will be thrown.
 * @author Zhanxin Yang
 */
@CompileStatic
class AuthenticateClient implements Action {
    private static final String INTERNAL_HEADER_NAME = 'oculus-internal'
    /**
     * The clientRepository to handle the client related logic.
     */
    private ClientRepository clientRepository

    private PasswordEncoder passwordEncoder

    @Required
    void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository
    }

    @Required
    void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder
    }

    /**
     * Override the {@link com.junbo.langur.core.webflow.action.Action}.execute method.
     * @param context The ActionContext contains the execution context.
     * @return The ActionResult contains the transition or other kind of result contains in a map.
     */
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        // Get the basic context from constructing an ActionContextWrapper.
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap
        def headerMap = contextWrapper.headerMap

        String clientId, clientSecret
        String authorization = headerMap.getFirst(OAuthParameters.AUTHORIZATION)

        // First try to extract the client credentials from the Authorization header.
        // The client credentials are presented in Basic scheme.
        // Format: clientId:clientSecret then Base64 encoded.
        if (StringUtils.hasText(authorization)) {
            def clientCredential = AuthorizationHeaderUtil.extractClientCredential(authorization)

            clientId = clientCredential.clientId
            clientSecret = clientCredential.clientSecret
        }

        // The authorization header is not presented, try to get the client id and client secret from the
        // query parameter.
        if (clientId == null) {
            clientId = parameterMap.getFirst(OAuthParameters.CLIENT_ID)
        }

        if (!StringUtils.hasText(clientId)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('client_id').exception()
        }

        Client appClient = clientRepository.getClient(clientId)

        if (appClient == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_id', clientId).exception()
        }

        if (clientSecret == null) {
            clientSecret = parameterMap.getFirst(OAuthParameters.CLIENT_SECRET)
        }

        if (!StringUtils.hasText(clientSecret)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('client_secret').exception()
        }

        // Validate the client secret in the parameter with the client secret in the configuration.
        if (!passwordEncoder.matches(appClient.clientSecret, passwordEncoder.encode(clientSecret))) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('client_secret', "*****").exception()
        }

        if (Boolean.TRUE.equals(appClient.internal)) {
            String internal = headerMap.getFirst(INTERNAL_HEADER_NAME)
            if (!Boolean.TRUE.equals(Boolean.parseBoolean(internal))) {
                throw AppCommonErrors.INSTANCE
                        .forbiddenWithMessage('This client is for internal use only').exception()
            }
        }
        // Validation passed, save the client in the actionContext.
        contextWrapper.client = appClient

        return Promise.pure(null)
    }
}
