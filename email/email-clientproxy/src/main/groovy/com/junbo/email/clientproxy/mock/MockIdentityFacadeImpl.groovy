package com.junbo.email.clientproxy.mock

import com.junbo.common.id.UserId
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise

/**
 * Created by Wei on 4/9/14.
 */
class MockIdentityFacadeImpl implements IdentityFacade {

    Promise<User> getUser(Long userId) {
        User user = new User()
        user.setId(new UserId(userId))
        user.setUserName('fake_user')
        user.setPassword('fake_password')
        user.setStatus('ACTIVE')
        return Promise.pure(user)
    }
}