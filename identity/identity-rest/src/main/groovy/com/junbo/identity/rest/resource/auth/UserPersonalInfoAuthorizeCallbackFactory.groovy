/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.auth

import com.junbo.authorization.AbstractAuthorizeCallbackFactory
import com.junbo.authorization.AuthorizeCallback
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * ItemAuthorizeCallbackFactoryBean.
 */
@CompileStatic
class UserPersonalInfoAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<UserPersonalInfo> {

    @Override
    AuthorizeCallback<UserPersonalInfo> create(UserPersonalInfo entity) {
        return new UserPersonalInfoAuthorizeCallback(this, entity)
    }
}
