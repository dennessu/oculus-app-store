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
import com.junbo.oauth.spec.model.Consent
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * SaveConsent.
 */
@CompileStatic
class SaveConsent implements Action {
    private ConsentRepository consentRepository

    @Required
    void setConsentRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def client = contextWrapper.client
        def oauthInfo = contextWrapper.oauthInfo
        def loginState = contextWrapper.loginState
        def consent = contextWrapper.consent

        if (consent == null) {
            consent = new Consent(
                    userId: loginState.userId,
                    clientId: client.clientId,
                    scopes: oauthInfo.scopes
            )

            consentRepository.saveConsent(consent)
        } else {
            consent.scopes = oauthInfo.scopes
            def oldConsent = consentRepository.getConsent(consent.userId, consent.clientId)
            consentRepository.updateConsent(consent, oldConsent)
        }

        return Promise.pure(new ActionResult('success'))
    }
}
