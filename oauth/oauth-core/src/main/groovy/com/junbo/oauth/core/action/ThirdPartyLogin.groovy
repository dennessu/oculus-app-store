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
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.identity.spec.v1.resource.AuthenticatorResource
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.spec.model.LoginState
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

/**
 * ThirdPartyLogin.
 */
@CompileStatic
abstract class ThirdPartyLogin implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyLogin)
    protected AuthenticatorResource authenticatorResource

    protected UserResource userResource

    protected UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setAuthenticatorResource(AuthenticatorResource authenticatorResource) {
        this.authenticatorResource = authenticatorResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    abstract Promise<ThirdPartyAccount> getThirdPartyAccount(ActionContextWrapper contextWrapper);

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        return getThirdPartyAccount(contextWrapper).recover { Throwable e ->
            LOGGER.error('Error getting the third party account', e)
            return Promise.pure(null)
        }.then { ThirdPartyAccount thirdPartyAccount ->
            if (thirdPartyAccount == null) {
                return Promise.pure(new ActionResult('error'))
            }

            AuthenticatorListOptions options = new AuthenticatorListOptions(
                    type: thirdPartyAccount.type,
                    externalId: thirdPartyAccount.externalId
            )

            return authenticatorResource.list(options).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { Results<UserAuthenticator> results ->
                if (results == null || results.items == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                if (results.items.isEmpty()) {
                    return createUserAuthenticator(contextWrapper, thirdPartyAccount)
                }

                return handleAuthenticator(contextWrapper, results.items.get(0))
            }
        }
    }

    protected Promise<ActionResult> createUserAuthenticator(ActionContextWrapper contextWrapper,
                                                            ThirdPartyAccount thirdPartyAccount) {
        User user = new User(isAnonymous: true)

        return userResource.create(user).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { User newUser ->
            if (newUser == null) {
                return Promise.pure(new ActionResult('error'))
            }

            user = newUser
            return Promise.pure(new ActionResult('next'))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            UserName name = new UserName(
                    firstName: thirdPartyAccount.firstName,
                    lastName: thirdPartyAccount.lastName,
                    nickName: thirdPartyAccount.nickName
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

                user.name = new UserPersonalInfoLink(
                        isDefault: true,
                        value: newUserPii.id as UserPersonalInfoId
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

                return Promise.pure(new ActionResult('next'))
            }
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            def authenticator = new UserAuthenticator(
                    userId: user.id as UserId,
                    type: thirdPartyAccount.type,
                    externalId: thirdPartyAccount.externalId
            )

            return authenticatorResource.create(authenticator).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserAuthenticator newAuthenticator ->
                if (newAuthenticator == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                return handleAuthenticator(contextWrapper, newAuthenticator)
            }
        }
    }

    protected static Promise<ActionResult> handleAuthenticator(ActionContextWrapper contextWrapper,
                                                               UserAuthenticator authenticator) {
        LoginState loginState = new LoginState(
                userId: (authenticator.userId as UserId).value,
                lastAuthDate: new Date()
        )

        contextWrapper.loginState = loginState

        return Promise.pure(new ActionResult('success'))
    }

    protected static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppExceptions.INSTANCE.errorCallingIdentity().error())
        }
    }


}

@CompileStatic
public class ThirdPartyAccount {
    String type
    String externalId
    String firstName
    String lastName
    String nickName
}
