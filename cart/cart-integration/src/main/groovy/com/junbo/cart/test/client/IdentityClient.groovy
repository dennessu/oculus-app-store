package com.junbo.cart.test.client
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.resource.UserResource
import groovy.transform.CompileStatic
import org.apache.commons.lang.RandomStringUtils

/**
 * Created by fzhang@wan-san.com on 14-2-24.
 */
@CompileStatic
class IdentityClient {

    UserResource userResource

    User randomUser() {
        User user = new User(
                username: RandomStringUtils.randomAlphabetic(10),
                isAnonymous: false
        )
        return userResource.create(user).wrapped().get()
    }
}
