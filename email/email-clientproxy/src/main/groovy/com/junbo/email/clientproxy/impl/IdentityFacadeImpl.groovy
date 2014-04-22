/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.email.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.resource.UserPiiResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Impl of IdentityFacade.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Resource(name = 'emailIdentityUserClient')
    private UserResource userResource

    @Resource(name='emailIdentityUserPiiClient')
    private UserPiiResource userPiiResource

    Promise<User> getUser(Long userId) {
        userResource.get(new UserId(userId), new UserGetOptions()).recover {
            throw AppErrors.INSTANCE.invalidUserId('').exception()
        }.then {
            return Promise.pure(it)
        }
    }

    Promise<UserPii>  getUserPii(Long userId) {
        def options = new UserPiiListOptions()
        options.userId = new UserId(userId)
        userPiiResource.list(options).recover {
            throw AppErrors.INSTANCE.invalidUserId('').exception()
        }.then {
            def userPii = it as Results<UserPii>
            if (userPii?.items?.size() != 0) {
                return Promise.pure(userPii.items.get(0))
            }
            return Promise.pure(null)
        }
    }
}
