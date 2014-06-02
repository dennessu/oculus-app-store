/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.auth

import com.junbo.authorization.AbstractAuthorizeCallback
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 5/14/2014.
 */
@CompileStatic
class UserPropertyAuthorizeCallback extends AbstractAuthorizeCallback<User> {

    UserPropertyAuthorizeCallback(UserPropertyAuthorizeCallbackFactory factory, User entity) {
        super(factory, entity)
    }

    @Override
    String getApiName() {
        return 'user-property'
    }

    @Override
    protected UserId getUserOwnerId() {
        return ((User) entity).id as UserId
    }
}
