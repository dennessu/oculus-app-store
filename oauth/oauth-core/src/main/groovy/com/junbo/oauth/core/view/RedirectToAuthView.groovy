/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.model.ViewModel
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

/**
 * RedirectToAuthView.
 */
@CompileStatic
class RedirectToAuthView extends AbstractView {

    private String authorizeUrl

    @Required
    void setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {

        def uriBuilder = UriComponentsBuilder.fromHttpUrl(authorizeUrl)
        uriBuilder.queryParam(OAuthParameters.CONVERSATION_ID, context.conversationId)

        def model = new ViewModel(
                view: 'redirect',
                model: [
                        "location": uriBuilder.build().toUriString()
                ] as Map<String, Object>
        )

        return Promise.pure(model)
    }
}
