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
    static void setCookie(ActionContext context, String cookieName,
                          String value, int maxAge = NewCookie.DEFAULT_MAX_AGE, boolean httpOnly = true) {
        def wrapper = new ActionContextWrapper(context)

        def secure = false // todo: read it from configuration service

        NewCookie cookie = new NewCookie(cookieName, value,
                null, null, null, maxAge, secure, httpOnly)

        List<NewCookie> responseCookieList = wrapper.responseCookieList
        responseCookieList.add(cookie)
    }

    static void clearCookie(ActionContext context, String cookieName) {
        setCookie(context, cookieName, null, 0)
    }
}
