/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.identity.spec.v1.model.User
import groovy.transform.CompileStatic

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
class UserAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<User> {

    @Override
    AuthorizeCallback<User> create(User entity) {
        return new UserAuthorizeCallback(this, entity)
    }
}
