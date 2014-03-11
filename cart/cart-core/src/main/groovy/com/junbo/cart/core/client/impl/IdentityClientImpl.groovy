/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.core.client.impl

import com.junbo.cart.core.client.IdentityClient
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
@CompileStatic
class IdentityClientImpl implements IdentityClient {

    private UserResource userResource

    void setIdentityUrl(String identityUrl) {
        this.identityUrl = identityUrl
    }

    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<User> getUser(UserId userId) {
        return userResource.getUser(userId)
    }
}
