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
import groovy.transform.CompileStatic
import org.springframework.util.Assert
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

/**
 * Redirect.
 */
@CompileStatic
class Redirect implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        // redirectUriBuilder is request scope
        // redirectUri is flow scope
        def contextWrapper = new ActionContextWrapper(context)
        UriComponentsBuilder builder = contextWrapper.redirectUriBuilder
        URI uri
        if (builder != null) {
            uri = builder.build().toUri()
        }
        else {
            uri = UriComponentsBuilder.fromUriString(contextWrapper.redirectUri).build().toUri()
        }

        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND).location(uri)
        contextWrapper.responseBuilder = responseBuilder

        return Promise.pure(null)
    }
}
