package com.junbo.order.clientproxy

import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.TestBuilder
import com.junbo.order.clientproxy.identity.IdentityFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by LinYi on 14-2-25.
 */
@CompileStatic
@TypeChecked
class IdentityFacadeTest extends BaseTest {
    @Resource(name = 'mockIdentityFacade')
    IdentityFacade identityFacade

    @Test
    void testGetUser() {
        User user = TestBuilder.buildUser()
        Promise createUserPromise = identityFacade.createUser(user)

        createUserPromise?.then(new Promise.Func<User, Promise>() {
            @Override
            Promise apply(User createdUser) {
                assert (createdUser != null)
                def getUserPromise = identityFacade.getUser(user.getId().getValue())
                getUserPromise?.then(new Promise.Func<User, Promise>() {
                    @Override
                    Promise apply(User returnedUser) {
                        assert (user.getId().getValue() == returnedUser.getId().value)
                    }
                } )
            }
        } )
        assert (createUserPromise != null)
    }
}
