/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.identity.spec.v1.resource.UserTFAAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.ExceptionUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * VerifyTFA.
 */
@CompileStatic
class VerifyTFA implements Action {
    private UserTFAAttemptResource userTFAAttemptResource

    @Required
    void setUserTFAAttemptResource(UserTFAAttemptResource userTFAAttemptResource) {
        this.userTFAAttemptResource = userTFAAttemptResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def userTFA = contextWrapper.userTFA
        def parameterMap = contextWrapper.parameterMap

        Assert.notNull(userTFA, 'userTFA is null')

        String tfaCode = parameterMap.getFirst(OAuthParameters.TFA_CODE)

        if (StringUtils.isEmpty(tfaCode)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired(OAuthParameters.TFA_CODE).error())
            return Promise.pure(new ActionResult('error'))
        }

        UserTFAAttempt attempt = new UserTFAAttempt(
                userId: userTFA.getUserId(),
                userTFAId: userTFA.getId(),
                verifyCode: tfaCode
        )

        return userTFAAttemptResource.create(userTFA.getUserId(), attempt).recover { Throwable e ->
            ExceptionUtil.handleIdentityException(e, contextWrapper, false)
            return Promise.pure(null)
        }.then { UserTFAAttempt newAttempt ->
            if (newAttempt == null) {
                return Promise.pure(new ActionResult('error'))
            }

            if (newAttempt.succeeded) {
                return Promise.pure(new ActionResult('success'))
            }

            contextWrapper.errors.add(AppCommonErrors.INSTANCE.
                    fieldInvalid(OAuthParameters.TFA_CODE, 'The TFA code is invalid').error())
            return Promise.pure(new ActionResult('error'))
        }
    }
}
