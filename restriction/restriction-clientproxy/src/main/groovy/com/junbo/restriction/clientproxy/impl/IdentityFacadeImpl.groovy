/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.restriction.clientproxy.IdentityFacade

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

    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    Promise<User> getUser(Long userId) {
        return userResource.get(new UserId(userId), new UserGetOptions())
    }
}
