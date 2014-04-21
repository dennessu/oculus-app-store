/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserPiiResource
import com.junbo.restriction.clientproxy.IdentityFacade
import com.junbo.restriction.spec.error.AppErrors

import javax.annotation.Resource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise

/**
 * Impl of Identity Facade.
 */
class IdentityFacadeImpl implements IdentityFacade {

    @Resource(name = 'restrictionIdentityUserClient')
    private UserResource userResource

    @Resource(name = 'restrictionIdentityUserPiiClient')
    private UserPiiResource userPiiResource

    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    void setUserPiiResource(UserPiiResource userPiiResource) {
        this.userPiiResource = userPiiResource
    }

    Promise<User> getUser(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions())
    }

    Promise<UserPii> getUserPii(Long userId) {
        def options = new UserPiiListOptions()
        options.userId = new UserId(userId)
        userPiiResource.list(options).recover {
            throw AppErrors.INSTANCE.invalidUserPii().exception()
        }.then {  Results<UserPii> userPii ->
            if (userPii?.items?.size() != 0) {
                return Promise.pure(userPii.items.get(0))
            }
            return Promise.pure(null)
        }
    }
}
