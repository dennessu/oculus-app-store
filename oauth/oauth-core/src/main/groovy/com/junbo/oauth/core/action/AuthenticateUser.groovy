/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.core.context.ServiceContext
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ServiceContextUtil
import com.junbo.oauth.db.repo.FlowStateRepository
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder

/**
 * Javadoc.
 */
@CompileStatic
class AuthenticateUser implements Action {
    private FlowStateRepository flowStateRepository

    private UserResource userResource

    @Required
    void setFlowStateRepository(FlowStateRepository flowStateRepository) {
        this.flowStateRepository = flowStateRepository
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    boolean execute(ServiceContext context) {
        def parameterMap = ServiceContextUtil.getParameterMap(context)

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)
        String password = parameterMap.getFirst(OAuthParameters.PASSWORD)

        if (!StringUtils.hasText(username)) {
            throw AppExceptions.INSTANCE.missingUsername().exception()
        }

        if (!StringUtils.hasText(password)) {
            throw AppExceptions.INSTANCE.missingPassword().exception()
        }

        Promise<User> userPromise = userResource.authenticateUser(username, password)

        if (userPromise == null || userPromise.wrapped() == null || userPromise.wrapped().get() == null) {
            throw AppExceptions.INSTANCE.invalidCredential().exception()
        }

        User user = userPromise.wrapped().get()

        def loginState = new LoginState(
                userId: user.id.value,
                lastAuthDate: new Date()
        )

        def flowState = ServiceContextUtil.getFlowState(context)

        if (flowState != null) {
            flowState.loginState = loginState
            flowStateRepository.saveOrUpdate(flowState)

            UriComponentsBuilder builder = ServiceContextUtil.getRedirectUriBuilder(context)

            if (builder == null) {
                builder = UriComponentsBuilder.fromHttpUrl(flowState.redirectUri)
                ServiceContextUtil.setRedirectUriBuilder(context, builder)
            }

            builder.queryParam(OAuthParameters.FLOW_STATE, flowState.id)
        }

        String rememberMe = parameterMap.getFirst(OAuthParameters.REMEMBER_ME)
        if ('TRUE'.equalsIgnoreCase(rememberMe)) {
            ServiceContextUtil.setNeedRememberMe(context, true)
        }

        ServiceContextUtil.setLoginState(context, loginState)
        return true
    }
}
