package com.junbo.cart.core.client.impl

import com.junbo.cart.core.client.IdentityClient
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by fzhang@wan-san.com on 14-2-18.
 */
@CompileStatic
class MockUserClientImpl implements IdentityClient {
    @Override
    Promise<User> getUser(UserId userId) {
        if (userId.value < 1000) {
            return Promise.pure(null)
        }
        def user = new User()
        user.id = userId
        user.status = 'ACTIVE'
        return Promise.pure(user)
    }
}
