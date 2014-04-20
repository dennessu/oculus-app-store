/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.mock

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.clientproxy.IdentityFacade

/**
 * Created by Wei on 4/20/14.
 */
class MockIdentityFacadeImpl implements IdentityFacade {
    Promise<User> getUser(Long userId) {
        User user = new User()
        user.active = true
        user.id = new UserId(userId)
        return Promise.pure(user)
    }

    Promise<UserPii> getUserPii(Long userId) {
        UserPii userPii = new UserPii()
        def calendar = Calendar.instance
        calendar.set(1994,0,12)
        userPii.birthday = calendar.getTime()
        return Promise.pure(userPii)
    }
}
