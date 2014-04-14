/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
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

    Promise<User> getUser(Long userId) {
        userResource.get(new UserId(userId), new UserGetOptions())
    }
}
