/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * VerifyEmail.
 */
@CompileStatic
class VerifyEmail implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyEmail)

    private EmailVerifyCodeRepository emailVerifyCodeRepository

    private UserResource userResource

    private UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setEmailVerifyCodeRepository(EmailVerifyCodeRepository emailVerifyCodeRepository) {
        this.emailVerifyCodeRepository = emailVerifyCodeRepository
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        String code = parameterMap.getFirst(OAuthParameters.EMAIL_VERIFY_CODE)

        if (StringUtils.isEmpty(code)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldRequired('evc').error())
            return Promise.pure(new ActionResult('error'))
        }

        EmailVerifyCode emailVerifyCode = contextWrapper.emailVerifyCode
        Assert.notNull(emailVerifyCode, 'emailVerifyCode is null')

        if (emailVerifyCode.code != code) {
            contextWrapper.errors.add(AppErrors.INSTANCE.invalidEmailVerifyCode().error())
            return Promise.pure(new ActionResult('error'))
        }

        return userResource.get(new UserId(emailVerifyCode.userId), new UserGetOptions()).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { User user ->
            if (user == null) {
                return Promise.pure(new ActionResult('error'))
            }

            def iter = user.emails.iterator()

            Closure process = null
            process = { ActionResult actionResult ->
                if (actionResult.id == 'error') {
                    return Promise.pure(actionResult)
                }

                if (!iter.hasNext() || actionResult.id == 'success') {
                    return Promise.pure(new ActionResult('success'))
                }

                def piiLink = iter.next()
                return userPersonalInfoResource.get(piiLink.value, new UserPersonalInfoGetOptions()).recover { Throwable e ->
                    handleException(e, contextWrapper)
                    return Promise.pure(null)
                }.then { UserPersonalInfo personalInfo ->
                    if (personalInfo == null) {
                        return Promise.pure(new ActionResult('error'))
                    }

                    Email email = ObjectMapperProvider.instance().treeToValue(personalInfo.value, Email)

                    if (email.info == emailVerifyCode.email) {
                        personalInfo.lastValidateTime = new Date()
                        personalInfo.value = ObjectMapperProvider.instance().valueToTree(email)
                        return userPersonalInfoResource.put(piiLink.value, personalInfo).recover { Throwable e ->
                            handleException(e, contextWrapper)
                            return Promise.pure(null)
                        }.then { UserPersonalInfo updatedPersonalInfo ->
                            if (updatedPersonalInfo == null) {
                                return Promise.pure(new ActionResult('error'))
                            }

                            return Promise.pure(new ActionResult('success'))
                        }
                    }
                    return Promise.pure(new ActionResult('next'))
                }.then(process)
            }

            return process(new ActionResult('next'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
