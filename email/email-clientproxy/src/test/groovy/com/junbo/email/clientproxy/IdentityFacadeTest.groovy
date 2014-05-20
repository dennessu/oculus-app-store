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

    @Test
    void testGetUserEmail() {
        def userEmail = identityFacade.getUserEmail(123L).get()
        assert userEmail != null
    }
}
