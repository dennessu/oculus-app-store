/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.UriBuilder
/**
 * RedirectToAuthView.
 */
@CompileStatic
class RedirectToAuthView extends AbstractView {
    private static final String AUTH_VIEW_PATH = 'oauth2/authorize'
    private URI baseUri

    @Required
    void setBaseUri(String baseUri) {
        this.baseUri = new URI(baseUri)
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        // In order to store the user cookie, we need to save the current conversation uri back to
        // the portal, then let the portal redirect user to the conversation uri.
        UriBuilder builder = UriBuilder.fromUri(baseUri)
        builder.path(AUTH_VIEW_PATH)

        builder.queryParam(OAuthParameters.CONVERSATION_ID, contextWrapper.conversationId)

        def model = new ViewModel(
                view: 'redirect',
                model: [
                        'location': builder.build().toString()
                ] as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
