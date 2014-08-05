package com.junbo.store.rest.test

import com.junbo.store.spec.model.login.UserCredential
import com.junbo.store.spec.model.login.UserCredentialRateRequest
import com.junbo.store.spec.model.login.UserNameCheckRequest
import com.junbo.store.spec.resource.LoginResource
import com.junbo.store.spec.resource.StoreResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
/**
 * The LoginApiTest class.
 */
@Test
@ContextConfiguration(locations = ['classpath:spring/store-rest-test-context.xml'])
class LoginApiTest extends AbstractTestNGSpringContextTests {

    @Autowired(required = true)
    @Qualifier('storeResourceClientProxy')
    StoreResource storeResource

    @Autowired(required = true)
    @Qualifier('loginResourceClientProxy')
    LoginResource loginResource

    @Test(enabled = false)
    public void testCreateUser() {
        assert storeResource != null
        //String username = StringRand
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        def result = loginResource.checkUserName(new UserNameCheckRequest(username: username, email: email)).get()
        assert result.status == 'SUCCESS'

        String password = Generator.genPassword()
        result = loginResource.rateUserCredential(new UserCredentialRateRequest(
                userCredential: new UserCredential(value: password, type: password)
        )).get()
        assert result.status == 'SUCCESS'

        def createUserRequest = Generator.genCreateUserRequest(username, password, email, Generator.genPIN())
        result = loginResource.createUser(createUserRequest).get()
        assert result.status == 'SUCCESS'
    }

}
