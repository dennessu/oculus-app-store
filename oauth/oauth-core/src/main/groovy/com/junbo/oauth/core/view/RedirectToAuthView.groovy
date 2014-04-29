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
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.web.util.UriComponentsBuilder

/**
 * RedirectToAuthView.
 */
@CompileStatic
class RedirectToAuthView extends AbstractView {

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def request = (ContainerRequest) contextWrapper.request

        // In order to store the user cookie, we need to save the current conversation uri back to
        // the portal, then let the portal redirect user to the conversation uri.
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(request.baseUri)
        builder.path(request.getPath(true))

        builder.queryParam(OAuthParameters.CONVERSATION_ID, contextWrapper.conversationId)

        def model = new ViewModel(
                view: 'redirect',
                model: [
                        'location': builder.build().toUriString()
                ] as Map<String, Object>
        )

        return Promise.pure(model)
    }
}
