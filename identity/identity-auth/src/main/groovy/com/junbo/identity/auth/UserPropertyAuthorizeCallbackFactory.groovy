/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * UserPropertyAuthorizeCallbackFactory.
 */
@CompileStatic
class UserPropertyAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<User> {

    @Override
    AuthorizeCallback<User> create(User entity) {
        return new UserPropertyAuthorizeCallback(this, entity)
    }

    AuthorizeCallback<User> create(UserId userId) {
        return create(new User(id: userId))
    }
}
