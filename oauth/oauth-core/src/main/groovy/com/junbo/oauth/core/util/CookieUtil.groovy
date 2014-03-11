/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest

import javax.ws.rs.core.NewCookie

/**
 * CookieUtil.
 */
@CompileStatic
class CookieUtil {
    static void setCookie(String cookieName, String value, int maxAge, ActionContext context) {
        def wrapper = new ActionContextWrapper(context)
        def request = wrapper.request
        URI uri = ((ContainerRequest) request).baseUri

        NewCookie cookie = new NewCookie(cookieName, value, uri.path, uri.host, null, maxAge, uri.scheme == 'https')

        List<NewCookie> responseCookieList = wrapper.responseCookieList
        responseCookieList.add(cookie)
    }

    static void clearCookie(String cookieName, ActionContext context) {
        setCookie(cookieName, null, 0, context)
    }
}
