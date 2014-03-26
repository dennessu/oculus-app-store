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
import com.junbo.oauth.db.repo.ConsentRepository
import com.junbo.oauth.spec.model.Prompt
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

/**
 * ValidateConsent.
 */
@CompileStatic
class ValidateConsent implements Action {
    private ConsentRepository consentRepository
    private String consentUri

    @Required
    void setConsentRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository
    }

    @Required
    void setConsentUri(String consentUri) {
        this.consentUri = consentUri
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def client = contextWrapper.client

        if (!client.needConsent) {
            return Promise.pure(new ActionResult('success'))
        }

        def loginState = contextWrapper.loginState

        def consent = consentRepository.getConsent(loginState.userId, client.clientId)

        if (consent == null) {
            return Promise.pure(redirectToConsent(contextWrapper))
        }

        contextWrapper.consent = consent

        def oauthInfo = contextWrapper.oauthInfo
        String[] additionalScopes = oauthInfo.scopes.findAll { String scope ->
            !consent.scopes.contains(scope)
        }

        if (additionalScopes.length > 0) {
            return Promise.pure(redirectToConsent(contextWrapper))
        }

        if (oauthInfo.prompts.contains(Prompt.CONSENT)) {
            return Promise.pure(redirectToConsent(contextWrapper))
        }

        return Promise.pure(new ActionResult('success'))
    }

    private ActionResult redirectToConsent(ActionContextWrapper context) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(consentUri)

        uriBuilder.queryParam(OAuthParameters.CONVERSATION_ID, context.conversationId)
        uriBuilder.queryParam(OAuthParameters.EVENT, 'consent')
        uriBuilder.queryParam(OAuthParameters.CLIENT_ID, context.client.clientId)

        context.redirectUriBuilder = uriBuilder
        return new ActionResult('redirectToConsent')
    }
}
