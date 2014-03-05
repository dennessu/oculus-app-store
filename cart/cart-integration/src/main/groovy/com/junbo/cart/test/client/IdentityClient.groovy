package com.junbo.cart.test.client

import com.junbo.identity.spec.model.user.User
import com.junbo.identity.spec.resource.proxy.UserResourceClientProxy
import org.apache.commons.lang.RandomStringUtils

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
class IdentityClient extends BaseClient {

    User randomUser() {
        User user = new User()
        user.userName = RandomStringUtils.randomAlphabetic(10) + '@wan-san.com'
        user.password = '123456!@a'
        user.passwordStrength = 'FAIR'
        user.status = 'ACTIVE'
        return new UserResourceClientProxy(asyncHttpClient, messageTranscoder, baseUrl).postUser(user).wrapped().get()
    }
}
