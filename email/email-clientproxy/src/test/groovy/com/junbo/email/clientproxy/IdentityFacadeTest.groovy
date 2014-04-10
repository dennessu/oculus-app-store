package com.junbo.email.clientproxy

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource

/**
 * IdentityFacade Test.
 */
@CompileStatic
@TypeChecked
class IdentityFacadeTest extends BaseTest {
    @Resource(name = 'mockIdentityFacade')
    private IdentityFacade identityFacade

    @Test(enabled = false)
    void testGetUser() {
        def user = identityFacade.getUser(123L).wrapped().get()
        assert user != null
        assert user.status == 'ACTIVE'
    }
}
