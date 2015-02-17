/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import groovy.transform.CompileStatic

import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response.ResponseBuilder

/**
 * CookieUtil.
 */
@CompileStatic
class CookieUtil {
    static boolean SECURE = ConfigServiceManager.instance().getConfigValueAsBool('oauth.cookie.secure', false)

    static void setCookie(ActionContext context, String cookieName,
                          String value, int maxAge = NewCookie.DEFAULT_MAX_AGE, boolean httpOnly = true) {
        def wrapper = new ActionContextWrapper(context)

        NewCookie cookie = new NewCookie(cookieName, value,
                null, null, null, maxAge, SECURE, httpOnly)

        List<NewCookie> responseCookieList = wrapper.responseCookieList
        responseCookieList.add(cookie)
    }

    static void setCookie(ResponseBuilder responseBuilder, String cookieName,
                          String value, int maxAge = NewCookie.DEFAULT_MAX_AGE, boolean httpOnly = true) {
        NewCookie cookie = new NewCookie(cookieName, value,
                null, null, null, maxAge, SECURE, httpOnly)
        responseBuilder.cookie(cookie)
    }

    static void clearCookie(ActionContext context, String cookieName) {
        setCookie(context, cookieName, null, 0)
    }
}
