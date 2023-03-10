/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class UserAuthorizeCallback extends AbstractAuthorizeCallback<User> {

    UserAuthorizeCallback(UserAuthorizeCallbackFactory factory, User entity) {
        super(factory, entity)
    }

    @Override
    String getApiName() {
        return 'users'
    }

    @Override
    protected UserId getUserOwnerId() {
        return (UserId) ((User) entity).id
    }
}
