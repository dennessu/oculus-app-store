package com.junbo.order.clientproxy

import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.TestBuilder
import com.junbo.order.clientproxy.identity.IdentityFacade
import groovy.transform.CompileStatic
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * Created by LinYi on 14-2-25.
 */
@CompileStatic
class IdentityFacadeTest extends BaseTest {
    @Resource(name = 'mockIdentityFacade')
    IdentityFacade identityFacade

    @Test
    void testGetUser() {
        def user = TestBuilder.buildUser()
        def createUserPromise = identityFacade.createUser(user)

        createUserPromise?.then(new Promise.Func<User, Promise>() {
            @Override
            Promise apply(User createdUser) {
                assert (createdUser != null)
                def getUserPromise = identityFacade.getUser(user.id.value)
                getUserPromise?.then(new Promise.Func<User, Promise>() {
                    @Override
                    Promise apply(User returnedUser) {
                        assert (user.id.value == returnedUser.id.value)
                    }
                } )
            }
        } )
        assert (createUserPromise != null)
    }
}
