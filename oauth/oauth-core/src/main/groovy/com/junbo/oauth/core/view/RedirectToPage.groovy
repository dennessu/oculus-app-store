/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * RedirectToPage.
 */
@CompileStatic
class RedirectToPage implements Action {

    private String pageUrl

    @Required
    void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def uriBuilder = UriComponentsBuilder.fromHttpUrl(pageUrl)
        uriBuilder.queryParam(OAuthParameters.CONVERSATION_ID, context.conversationId)

        contextWrapper.responseBuilder = Response.status(Response.Status.FOUND)
                .location(uriBuilder.build().toUri())

        return Promise.pure(null)
    }
}
