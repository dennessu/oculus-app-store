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
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * CreateAnonymousUser.
 */
@CompileStatic
class CreateAnonymousUser implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateAnonymousUser)

    private UserResource userResource

    private UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def thirdPartyAccount = contextWrapper.thirdPartyAccount

        Assert.notNull(thirdPartyAccount, 'thirdPartyAccount is null')
        User user = new User(
                isAnonymous: true,
                nickName: thirdPartyAccount.nickName
        )

        return userResource.create(user).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { User newUser ->
            if (newUser == null) {
                return Promise.pure(new ActionResult('error'))
            }

            user = newUser
            contextWrapper.user = user
            return Promise.pure(new ActionResult('next'))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            UserName name = new UserName(
                    givenName: thirdPartyAccount.firstName,
                    familyName: thirdPartyAccount.lastName
            )

            UserPersonalInfo namePii = new UserPersonalInfo(
                    userId: user.id as UserId,
                    type: 'NAME',
                    value: ObjectMapperProvider.instance().valueToTree(name)
            )

            return userPersonalInfoResource.create(namePii).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserPersonalInfo newUserPii ->
                if (newUserPii == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                user.name = newUserPii.id as UserPersonalInfoId

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
