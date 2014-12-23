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
import com.junbo.oauth.core.util.OAuthInfoUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

import javax.ws.rs.core.Response

/**
 * RedirectToPage.
 */
@CompileStatic
class RedirectToClientError implements Action {

    private String errorMessage

    private String pageUrl

    @Required
    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage
    }

    void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        UriComponentsBuilder uriBuilder

        // Redirect back to the client redirect uri with error message
        if ('true'.equalsIgnoreCase(contextWrapper.extraParameterMap.get(OAuthParameters.HANDLE_ERROR)) || StringUtils.isEmpty(this.pageUrl)) {
            def oauthInfo = contextWrapper.oauthInfo
            uriBuilder = UriComponentsBuilder.fromHttpUrl(oauthInfo.redirectUri)

            Map<String, String> parameters = new HashMap<>()
            parameters.put(OAuthParameters.ERROR, errorMessage)

            // Add the state parameter.
            if (oauthInfo.state != null) {
                parameters.put(OAuthParameters.STATE, oauthInfo.state)
            }

            if (OAuthInfoUtil.isImplicitFlow(oauthInfo)) {
                List<GString> fragments = parameters.collect { String key, String value ->
                    return "$key=$value"
                }

                uriBuilder.fragment(StringUtils.arrayToDelimitedString(fragments.toArray(), '&'))
            } else {
                parameters.each { String key, String value ->
                    uriBuilder.queryParam(key, value)
                }
            }
        // Directly redirect to a certain page
        } else {
            // the pageUrl should not be null
            String realUrl = new String(pageUrl)
            if (contextWrapper.viewCountry != null) {
                realUrl = realUrl.replaceFirst('/country', '/' + contextWrapper.viewCountry)
            }
            if (contextWrapper.viewLocale != null) {
                realUrl = realUrl.replaceFirst('/locale', '/' + contextWrapper.viewLocale)
            }

            uriBuilder = UriComponentsBuilder.fromHttpUrl(realUrl)
        }

        contextWrapper.responseBuilder = Response.status(Response.Status.FOUND)
                .location(uriBuilder.build().toUri())

        return Promise.pure(null)
    }
}
