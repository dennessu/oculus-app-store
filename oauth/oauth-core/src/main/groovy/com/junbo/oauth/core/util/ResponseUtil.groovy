/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest

import javax.ws.rs.core.NewCookie

/**
 * ResponseUtil.
 */
@CompileStatic
class ResponseUtil {
    public final static Closure WRITE_RESPONSE_CLOSURE = { ActionContext context ->
        ActionContextWrapper wrapper = new ActionContextWrapper(context)
        def responseBuilder = wrapper.responseBuilder
        def cookieList = wrapper.responseCookieList
        def headerMap = wrapper.responseHeaderMap

        cookieList.each { NewCookie cookie ->
            responseBuilder.cookie(cookie)
        }

        headerMap.each { String key, String value ->
            responseBuilder.header(key, value)
        }

        return Promise.pure(responseBuilder.build())
    }
}
