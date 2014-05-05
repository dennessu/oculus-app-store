/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.mock

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-2-27.
 */
@CompileStatic
class MockIdentityFacadeImpl implements IdentityFacade {
    @Override
    Promise<User> getUser(Long userId) {
        User user = new User()
        user.setStatus('ACTIVE')
        user.setId(new UserId(12345))
        return Promise.pure(user)
    }

    @Override
    Promise<Address> getAddress(Long addressId) {
        return Promise.pure(null)
    }
}
