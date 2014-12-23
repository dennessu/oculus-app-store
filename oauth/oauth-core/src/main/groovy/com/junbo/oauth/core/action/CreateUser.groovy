/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.core.util.ExceptionUtil
import com.junbo.oauth.core.util.ValidatorUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * CreateUser.
 */
@CompileStatic
class CreateUser implements Action {
    private UserResource userResource
    private UserPersonalInfoResource userPersonalInfoResource
    private CountryResource countryResource

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String username = parameterMap.getFirst(OAuthParameters.USERNAME)
        String countryOfResidence = parameterMap.getFirst(OAuthParameters.COUNTRY)
        String preferLocale = contextWrapper.viewLocale
        if (preferLocale != null) {
            preferLocale = preferLocale.replace('-', '_')
        }

        // check username has been created or not
        if (contextWrapper.user != null && contextWrapper.username == username) {
            return Promise.pure(new ActionResult('success'))
        }

        if (countryOfResidence != null && !ValidatorUtil.isValidCountryCode(countryOfResidence, countryResource)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterInvalid('cor').error())
            return Promise.pure(null)
        }

        User user = new User(
                isAnonymous: true,
                nickName: username,
                countryOfResidence: countryOfResidence == null ? null : new CountryId(countryOfResidence),
                preferredLocale: preferLocale == null ? null : new LocaleId(preferLocale)
        )

        return userResource.create(user).recover { Throwable throwable ->
            if (throwable instanceof AppErrorException) {
                contextWrapper.errors.add(((AppErrorException) throwable).error.error())
            } else {
                contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
            }

            return Promise.pure(null)
        }.then { User newUser ->
            if (newUser == null) {
                return Promise.pure(new ActionResult('error'))
            }

            UserLoginName userLoginName = new UserLoginName(
                    userName: username
            )

            UserPersonalInfo userLoginNamePerInfo = new UserPersonalInfo(
                    userId: newUser.getId(),
                    type: 'USERNAME',
                    value: ObjectMapperProvider.instance().valueToTree(userLoginName)
            )

            return userPersonalInfoResource.create(userLoginNamePerInfo).recover { Throwable throwable ->
                ExceptionUtil.handleIdentityException(throwable, contextWrapper, false)
                return Promise.pure(null)
            }.then { UserPersonalInfo personalInfo ->
                if (personalInfo == null) {
                    return Promise.pure(new ActionResult('error'))
                }
                newUser.username = personalInfo.getId()
                newUser.isAnonymous = false

                return userResource.put(newUser.getId(), newUser).recover { Throwable throwable ->
                    ExceptionUtil.handleIdentityException(throwable, contextWrapper, false)
                    return Promise.pure(null)
                }.then { User createdUser ->
                    if (createdUser == null) {
                        return Promise.pure(new ActionResult('error'))
                    }
                    contextWrapper.user = createdUser
                    contextWrapper.username = username
                    return Promise.pure(new ActionResult('success'))
                }
            }
        }
    }
}
