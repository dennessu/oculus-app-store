package com.junbo.email.clientproxy.mock

import com.junbo.common.id.UserId
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise

/**
 * Created by Wei on 4/9/14.
 */
class MockIdentityFacadeImpl implements IdentityFacade {

    Promise<User> getUser(Long userId) {
        User user = new User()
        user.setId(new UserId(userId))
        user.setActive(true)
        user.setLocale('en_US')
        return Promise.pure(user)
    }
}