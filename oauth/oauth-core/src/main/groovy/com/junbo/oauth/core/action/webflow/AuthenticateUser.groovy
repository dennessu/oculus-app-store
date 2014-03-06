/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action.webflow

import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

/**
 * AuthenticateUser
 */
@CompileStatic
class AuthenticateUser implements Action {
    private UserResource userResource

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def parameterMap = contextWrapper.parameterMap

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)
        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (!StringUtils.hasText(username)) {
            throw AppExceptions.INSTANCE.missingUsername().exception()
        }

        if (!StringUtils.hasText(password)) {
            throw AppExceptions.INSTANCE.missingPassword().exception()
        }

        userResource.authenticateUser(username, password).then { User user ->
            if (user == null) {
                throw AppExceptions.INSTANCE.invalidCredential().exception()
            }

            def loginState = new LoginState(
                    userId: user.id.value,
                    lastAuthDate: new Date()
            )

            contextWrapper.loginState = loginState

            def conversationId = parameterMap.getFirst(OAuthParameters.CONVERSATION_ID)
            if (StringUtils.hasText(conversationId)) {
                String rememberMe = parameterMap.getFirst(OAuthParameters.REMEMBER_ME)
                if ('TRUE'.equalsIgnoreCase(rememberMe)) {
                    contextWrapper.needRememberMe = true
                }

                def request = (ContainerRequest) contextWrapper.request

                UriComponentsBuilder builder = UriComponentsBuilder.fromUri(request.baseUri)
                builder.path(request.getPath(true))

                builder.queryParam(OAuthParameters.CONVERSATION_ID, contextWrapper.conversationId)
                builder.queryParam(OAuthParameters.EVENT, 'loginSuccess')

                contextWrapper.redirectUriBuilder = builder

                return Promise.pure(new ActionResult('loginSuccess'))
            }

            return Promise.pure(null)
        }
    }
}
