/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserDOB
import com.junbo.identity.spec.v1.model.UserGender
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.spec.model.Gender
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * CreateUserPii.
 */
@CompileStatic
class CreateUserPii implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserPii)
    private UserPersonalInfoResource userPersonalInfoResource

    private UserResource userResource

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
        def user = contextWrapper.user

        Assert.notNull(user, 'user is null')

        String firstName = parameterMap.getFirst(OAuthParameters.FIRST_NAME)

        String lastName = parameterMap.getFirst(OAuthParameters.LAST_NAME)

        String email = parameterMap.getFirst(OAuthParameters.EMAIL)

        Gender gender = contextWrapper.gender

        Date dob = contextWrapper.dob

        UserName name = new UserName(givenName: firstName, familyName: lastName)

        UserPersonalInfo namePii = new UserPersonalInfo(
                userId: user.id as UserId,
                type: 'NAME',
                value: ObjectMapperProvider.instance().valueToTree(name)
        )

        UserPersonalInfo emailPii = new UserPersonalInfo(
                userId: user.id as UserId,
                type: 'EMAIL',
                value: ObjectMapperProvider.instance().valueToTree(new Email(info: email))
        )

        UserPersonalInfo genderPii = null

        if (gender != null) {
            genderPii = new UserPersonalInfo(
                    userId: user.id as UserId,
                    type: 'GENDER',
                    value: ObjectMapperProvider.instance().valueToTree(new UserGender(info: gender.name()))
            )
        }

        UserPersonalInfo dobPii = new UserPersonalInfo(
                userId: user.id as UserId,
                type: 'DOB',
                value: ObjectMapperProvider.instance().valueToTree(new UserDOB(info: dob))
        )

        return userPersonalInfoResource.create(namePii).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { UserPersonalInfo newNamePii ->
            if (newNamePii == null) {
                return Promise.pure(new ActionResult('error'))
            }

            user.name = new UserPersonalInfoLink(
                    isDefault: true,
                    value: newNamePii.id as UserPersonalInfoId
            )

            return Promise.pure(new ActionResult('next'))
        }.then { ActionResult result ->
             if (result.id == 'error') {
                return Promise.pure(result)
            }

            return userPersonalInfoResource.create(emailPii).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserPersonalInfo newEmailPii ->
                if (newEmailPii == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                user.emails = [new UserPersonalInfoLink(
                        isDefault: true,
                        value: newEmailPii.id as UserPersonalInfoId
                )]

                return Promise.pure(new ActionResult('next'))
            }
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            if (genderPii != null) {
                return userPersonalInfoResource.create(genderPii).recover { Throwable e ->
                    handleException(e, contextWrapper)
                    return Promise.pure(null)
                }.then { UserPersonalInfo newGenderPii ->
                    if (newGenderPii == null) {
                        return Promise.pure(new ActionResult('error'))
                    }

                    user.gender = new UserPersonalInfoLink(
                            isDefault: true,
                            value: newGenderPii.id as UserPersonalInfoId
                    )

                    return Promise.pure(new ActionResult('next'))
                }
            }

            return Promise.pure(new ActionResult('next'))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            return userPersonalInfoResource.create(dobPii).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserPersonalInfo newDobPii ->
                if (newDobPii == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                user.dob = new UserPersonalInfoLink(
                        isDefault: true,
                        value: newDobPii.id as UserPersonalInfoId
                )

                return Promise.pure(new ActionResult('next'))
            }
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            return userResource.put(user.id as UserId, user).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { User updatedUser ->
                if (updatedUser == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                contextWrapper.user = updatedUser

                LoginState loginState = new LoginState(
                        userId: ((UserId) user.id).value,
                        lastAuthDate: new Date()
                )

                contextWrapper.loginState = loginState
                return Promise.pure(new ActionResult('success'))
            }
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
