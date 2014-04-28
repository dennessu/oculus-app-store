/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.clientproxy.mock

import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.clientproxy.IdentityFacade
import groovy.transform.CompileStatic

/**
 * Created by Wei on 4/20/14.
 */
@CompileStatic
class MockIdentityFacadeImpl implements IdentityFacade {
    Promise<User> getUser(Long userId) {
        User user = new User()
        user.id = new UserId(userId)
        return Promise.pure(user)
    }

    Promise<Date> getUserDob(Long userId) {
        def calendar = Calendar.instance
        calendar.set(1994, 0, 12)
        return Promise.pure(calendar.time)
    }
}
