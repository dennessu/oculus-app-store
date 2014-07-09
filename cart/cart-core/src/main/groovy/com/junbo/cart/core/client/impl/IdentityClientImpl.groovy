/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.client.impl
import com.junbo.cart.core.client.IdentityClient
import com.junbo.cart.spec.error.AppErrors
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
@CompileStatic
class IdentityClientImpl implements IdentityClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityClientImpl)

    private UserResource userResource

    void setIdentityUrl(String identityUrl) {
        this.identityUrl = identityUrl
    }

    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<User> getUser(UserId userId) {
        return userResource.get(userId, new UserGetOptions()).syncRecover { Throwable throwable ->
            LOGGER.error('name=Cart_GetUser_Error', throwable)
        }.syncThen { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound("user", userId).exception()
            }
            return user
        }
    }
}
