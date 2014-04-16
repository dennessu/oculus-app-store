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

/**
 * RedirectToPage.
 */
@CompileStatic
class RedirectToClientError implements Action {

    private String errorMessage

    @Required
    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def oauthInfo = contextWrapper.oauthInfo
        def uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)
        uriBuilder.queryParam(OAuthParameters.ERROR, errorMessage)

        contextWrapper.redirectUriBuilder = uriBuilder
        return Promise.pure(null)
    }
}
