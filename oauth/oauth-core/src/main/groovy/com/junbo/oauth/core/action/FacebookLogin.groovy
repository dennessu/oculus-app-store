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
import com.junbo.oauth.clientproxy.facebook.FacebookAccount
import com.junbo.oauth.clientproxy.facebook.FacebookApi
import com.junbo.oauth.clientproxy.facebook.FacebookException
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
 * FacebookLogin.
 */
@CompileStatic
class FacebookLogin implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookLogin)
    private static final String FACEBOOK_AUTHENTICATOR_TYPE = 'FACEBOOK'

    private FacebookApi facebookApi

    @Required
    void setFacebookApi(FacebookApi facebookApi) {
        this.facebookApi = facebookApi
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String facebookAuth = parameterMap.getFirst(OAuthParameters.FACEBOOK_AUTH)

        if (StringUtils.isEmpty(facebookAuth)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingFacebookAuth().error())
            return Promise.pure(null)
        }

        return facebookApi.getAccountInfo(facebookAuth).recover { Throwable e ->
            if (e instanceof FacebookException) {
                String message = (e as FacebookException).getMessage()
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingFacebook(message).error());
            } else {
                LOGGER.error('Error calling Facebook api', e)
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingFacebook().error())
            }

            return Promise.pure(null)
        }.then { FacebookAccount facebookAccount ->
            if (facebookAccount == null) {
                return Promise.pure(null)
            }

            contextWrapper.thirdPartyAccount = new ThirdPartyAccount(
                    type: FACEBOOK_AUTHENTICATOR_TYPE,
                    externalId: facebookAccount.id,
                    firstName: facebookAccount.firstName,
                    lastName: facebookAccount.lastName,
                    nickName: facebookAccount.name
            )

            return Promise.pure(new ActionResult('success'))
        }
    }
}
