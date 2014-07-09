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
import com.junbo.oauth.clientproxy.google.GoogleAccount
import com.junbo.oauth.clientproxy.google.GoogleApi
import com.junbo.oauth.clientproxy.google.GoogleException
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.model.ThirdPartyAccount
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * GoogleLogin.
 */
@CompileStatic
class GoogleLogin implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleLogin)
    private static final String GOOGLE_AUTHENTICATOR_TYPE = 'GOOGLE'

    private GoogleApi googleApi

    @Required
    void setGoogleApi(GoogleApi googleApi) {
        this.googleApi = googleApi
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String googleAuth = parameterMap.getFirst(OAuthParameters.GOOGLE_AUTH)

        if (StringUtils.isEmpty(googleAuth)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingGoogleAuth().error())
            return Promise.pure(null)
        }

        return googleApi.getAccountInfo("Bearer $googleAuth").recover { Throwable e ->
            if (e instanceof GoogleException) {
                String message = (e as GoogleException).getMessage()
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingGoogle(message).error())
            } else {
                LOGGER.error('Error calling Google api', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingGoogle().error())
            }

            return Promise.pure(null)
        }.then { GoogleAccount googleAccount ->
            if (googleAccount == null) {
                return Promise.pure(null)
            }

            contextWrapper.thirdPartyAccount = new ThirdPartyAccount(
                    type: GOOGLE_AUTHENTICATOR_TYPE,
                    externalId: googleAccount.id,
                    firstName: googleAccount.name.givenName,
                    lastName: googleAccount.name.familyName,
                    nickName: googleAccount.displayName
            )

            return Promise.pure(new ActionResult('success'))
        }
    }
}
