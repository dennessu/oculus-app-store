/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.mock

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise

/**
 * Created by xmchen on 14-2-27.
 */
class MockIdentityFacadeImpl implements IdentityFacade {
    @Override
    Promise<User> getUser(Long userId) {
        User user = new User()
        user.setUserName('xmchen')
        user.setStatus('Active')
        user.setKey(new UserId(12345))
        return Promise.pure(user)
    }
}
