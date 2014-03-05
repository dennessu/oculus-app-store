/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.util.ServiceContextUtil
import groovy.transform.CompileStatic
import org.springframework.util.Assert
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * Javadoc.
 */
@CompileStatic
class Redirect implements Action {
    @Override
    boolean execute(ServiceContext context) {

        UriComponentsBuilder builder = ServiceContextUtil.getRedirectUriBuilder(context)
        Assert.notNull(builder)

        def responseBuilder = Response.status(Response.Status.FOUND).location(builder.build().toUri())
        ServiceContextUtil.setResponseBuilder(context, responseBuilder)

        return false
    }
}
