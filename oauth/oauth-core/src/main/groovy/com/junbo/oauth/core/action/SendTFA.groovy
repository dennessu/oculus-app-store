/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.identity.spec.v1.resource.UserTFAResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.util.ExceptionUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * SendTFA.
 */
@CompileStatic
class SendTFA implements Action {
    private UserTFAResource userTFAResource

    private UserResource userResource

    @Required
    void setUserTFAResource(UserTFAResource userTFAResource) {
        this.userTFAResource = userTFAResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def loginState = contextWrapper.loginState
        def locale = contextWrapper.viewLocale?:'en_US'
        def parameterMap = contextWrapper.parameterMap
        Assert.notNull(loginState, 'loginState is null')

        String event = parameterMap.getFirst(OAuthParameters.EVENT)
        Assert.isTrue(event.startsWith('send'))
        String verifyType = event.substring(4)

        return userResource.get(new UserId(loginState.userId), new UserGetOptions()).recover { Throwable e ->
            ExceptionUtil.handleIdentityException(e, contextWrapper, false)
            return Promise.pure(null)
        }.then { User user ->
            if (user == null) {
                return Promise.pure(new ActionResult('error'))
            }
            contextWrapper.user = user

            UserPersonalInfoLink personalInfo = null

            if (verifyType == 'EMAIL') {
                Assert.isTrue(user.emails != null && !user.emails.isEmpty())
                personalInfo = user.emails.find { UserPersonalInfoLink personalInfoLink ->
                    return personalInfoLink.isDefault
                }
            } else if (verifyType == 'SMS') {
                if (user.phones == null || user.phones.isEmpty()) {
                    contextWrapper.errors.add(AppErrors.INSTANCE.phoneNotFound().error())
                    return Promise.pure(new ActionResult('error'))
                }

                personalInfo = user.phones.find { UserPersonalInfoLink personalInfoLink ->
                    return personalInfoLink.isDefault
                }
            } else {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.fieldInvalid('verifyType').error())
                return Promise.pure(new ActionResult('error'))
            }

            UserTFA tfa = new UserTFA(
                    personalInfo: personalInfo.value,
                    sentLocale: new LocaleId(locale.replace('-', '_')),
                    verifyType: verifyType
            )

            return userTFAResource.create(user.getId(), tfa).recover { Throwable e ->
                ExceptionUtil.handleIdentityException(e, contextWrapper, false)
                return Promise.pure(null)
            }.then { UserTFA newTFA ->
                if (newTFA == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                contextWrapper.userTFA = newTFA

                return Promise.pure(null)
            }
        }
    }
}
