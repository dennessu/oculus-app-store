/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import groovy.transform.CompileStatic
import org.springframework.util.Assert
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * Redirect.
 */
@CompileStatic
class Redirect implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        UriComponentsBuilder builder = contextWrapper.redirectUriBuilder
        Assert.notNull(builder, 'builder is null')

        Response.ResponseBuilder responseBuilder = Response.status(Response.Status.FOUND)
                .location(builder.build().toUri())

        contextWrapper.responseBuilder = responseBuilder

        return Promise.pure(null)
    }
}
