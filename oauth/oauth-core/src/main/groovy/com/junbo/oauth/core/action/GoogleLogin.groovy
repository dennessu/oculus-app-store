/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.clientproxy.google.GoogleAccount
import com.junbo.oauth.clientproxy.google.GoogleApi
import com.junbo.oauth.clientproxy.google.GoogleException
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
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
class GoogleLogin extends ThirdPartyLogin {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleLogin)
    private static final String GOOGLE_AUTHENTICATOR_TYPE = 'GOOGLE'

    private GoogleApi googleApi

    @Required
    void setGoogleApi(GoogleApi googleApi) {
        this.googleApi = googleApi
    }

    @Override
    Promise<ThirdPartyAccount> getThirdPartyAccount(ActionContextWrapper contextWrapper) {
        def parameterMap = contextWrapper.parameterMap

        String googleAuth = parameterMap.getFirst(OAuthParameters.GOOGLE_AUTH)

        if (StringUtils.isEmpty(googleAuth)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingGoogleAuth().error())
            return Promise.pure(null)
        }

        return googleApi.getAccountInfo("Bearer $googleAuth").recover { Throwable e ->
            if (e instanceof GoogleException) {
                contextWrapper.errors.add((e as GoogleException).commonError())
            } else {
                LOGGER.error('Error calling Google api', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingGoogle().error())
            }

            return Promise.pure(null)
        }.then { GoogleAccount googleAccount ->
            if (googleAccount == null) {
                return Promise.pure(null)
            }

            return Promise.pure(new ThirdPartyAccount(
                    type: GOOGLE_AUTHENTICATOR_TYPE,
                    externalId: googleAccount.id,
                    firstName: googleAccount.name.givenName,
                    lastName: googleAccount.name.familyName,
                    nickName: googleAccount.displayName
            ))
        }
    }
}
