package com.junbo.store.rest.test.bvt

import com.junbo.common.error.AppErrorException
import com.junbo.store.rest.test.Generator
import com.junbo.store.spec.model.login.*
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
class LoginApiTest extends TestBase {

    @Test
    public void testBVT() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String refreshToken = null
        def result = loginResource.checkUserName(new UserNameCheckRequest(username: username)).get()
        assert result.isAvailable

        String password = Generator.genPassword()
        result = loginResource.rateUserCredential(new UserCredentialRateRequest(
                userCredential: new UserCredential(value: password, type: 'PASSWORD'),
                context: new UserCredentialRateContext(username: username))
        ).get()
        assert result.strength == 'STRONG'

        // create user with invalid password (not enough length)
        def createUserRequest = Generator.genCreateUserRequest(username, '123456', email, pin)
        try {
            result = loginResource.createUser(createUserRequest).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.101'
        }

        /*
        // create user with invalid password (only characters)
        createUserRequest = Generator.genCreateUserRequest(username, 'abcedfcbda', email, pin)
        try {
            result = loginResource.createUser(createUserRequest).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.001'
        }*/

        createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        result = loginResource.createUser(createUserRequest).get()
        assert result.username == username
        assert result.accessToken != null
        assert result.refreshToken != null
        def userId = result.userId
        refreshToken = result.refreshToken

        // login with invalid username
        try {
            loginResource.signIn(new UserSignInRequest(username : Generator.genUserName(), userCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '132.103'
        }

        // login with invalid password
        try {
            loginResource.signIn(new UserSignInRequest(username : username, userCredential:  new UserCredential(type: 'PASSWORD', value: "${password}123"))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '132.103'
        }

        // login
        result = loginResource.signIn(new UserSignInRequest(username: username, userCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
        assert result.username == username
        assert result.accessToken != null
        assert result.refreshToken != null
        assert result.userId == userId

        // get auth token
        result = loginResource.getAuthToken(new AuthTokenRequest(refreshToken: refreshToken)).get()
        refreshToken = result.refreshToken
        // assert result.userId == userId
        assert result.username == username
        loginResource.getAuthToken(new AuthTokenRequest(refreshToken: refreshToken)).get()

        // get with invalid auth token
        try {
            loginResource.getAuthToken(new AuthTokenRequest(refreshToken: refreshToken)).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '132.001'
        }

        result = loginResource.checkUserName(new UserNameCheckRequest(username: username)).get()
        assert !result.isAvailable

        result = loginResource.checkUserName(new UserNameCheckRequest(email: email)).get()
        assert !result.isAvailable
    }

    @Test
    public void testCheckUserNameInvalid() {
        try {
            loginResource.checkUserName(new UserNameCheckRequest(username: 'abc')).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.001'
        }

        try {
            loginResource.checkUserName(new UserNameCheckRequest(username: '123')).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.001'
        }

    }

    @Test
    public void testCheckEmailInvalid() {
        try {
            loginResource.checkUserName(new UserNameCheckRequest(email: 'abcbcde')).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.001'
        }
    }
}
