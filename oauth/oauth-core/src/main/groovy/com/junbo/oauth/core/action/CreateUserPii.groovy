/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.resource.UserPiiResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.model.Gender
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * CreateUserPii.
 */
@CompileStatic
class CreateUserPii implements Action {
    private UserPiiResource userPiiResource

    @Required
    void setUserPiiResource(UserPiiResource userPiiResource) {
        this.userPiiResource = userPiiResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def user = contextWrapper.user

        Assert.notNull(user, 'user is null')

        String firstName = parameterMap.getFirst(OAuthParameters.FIRST_NAME)

        String lastName = parameterMap.getFirst(OAuthParameters.LAST_NAME)

        Gender gender = contextWrapper.gender

        Date dob = contextWrapper.dob

        UserPii userPii = new UserPii(
                userId: (UserId) user.id,
                name: new UserName(firstName: firstName, lastName: lastName),
                birthday: dob,
                gender: gender.name(),
                displayName: "$firstName $lastName"
        )

        userPiiResource.create(userPii).recover { Throwable throwable ->
            if (throwable instanceof AppErrorException) {
                contextWrapper.errors.add(((AppErrorException) throwable).error.error())
            } else {
                contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
            }

            return Promise.pure(null)
        }.then { UserPii newUserPii ->
            if (newUserPii == null) {
                return Promise.pure(new ActionResult('error'))
            }

            contextWrapper.userPii = newUserPii

            LoginState loginState = new LoginState(
                    userId: ((UserId) user.id).value,
                    lastAuthDate: new Date()
            )

            contextWrapper.loginState = loginState
            return Promise.pure(null)
        }
    }
}
