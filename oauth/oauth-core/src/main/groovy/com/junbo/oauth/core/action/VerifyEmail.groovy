/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPiiId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserEmail
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.identity.spec.v1.resource.UserPiiResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * VerifyEmail.
 */
@CompileStatic
class VerifyEmail implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEmail)

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    private UserPiiResource userPiiResource

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Required
    void setUserPiiResource(UserPiiResource userPiiResource) {
        this.userPiiResource = userPiiResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        String code = (String) context.flowScope[OAuthParameters.CODE]

        if (StringUtils.isEmpty(code)) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.missingEmailVerifyCode().error())
            return Promise.pure(new ActionResult('error'))
        }

        EmailVerifyCode emailVerifyCode = emailVerifyCodeRepository.getAndRemove(code)

        if (emailVerifyCode == null) {
            contextWrapper.errors.add(AppExceptions.INSTANCE.invalidEmailVerifyCode(code).error())
            return Promise.pure(new ActionResult('error'))
        }

        UserPiiListOptions options = new UserPiiListOptions(userId: new UserId(emailVerifyCode.userId))
        userPiiResource.list(options).recover { Throwable e ->
            LOGGER.error('Error Calling the Identity service', e)
            return Promise.pure(null)
        }.then { Results<UserPii> results ->
            if (results == null) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
                return Promise.pure(new ActionResult('error'))
            }

            if (results.items == null || results.items.isEmpty()) {
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
                return Promise.pure(new ActionResult('error'))
            }

            UserPii userPii = results.items.get(0)

            UserEmail userEmail = userPii.emails.values().find { UserEmail email ->
                email.value == emailVerifyCode.email
            }

            userEmail.verified = true

            userPiiResource.put(userPii.id as UserPiiId, userPii).recover { Throwable e ->
                LOGGER.error('Error Calling the Identity service', e)
                return Promise.pure(null)
            }.then { UserPii userPiiUpdated ->
                if (userPiiUpdated == null) {
                    contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
                    return Promise.pure(new ActionResult('error'))
                }

                return Promise.pure(new ActionResult('success'))
            }
        }
    }
}
