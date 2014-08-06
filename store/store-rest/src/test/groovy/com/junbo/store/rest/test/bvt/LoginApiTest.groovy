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
@ContextConfiguration(locations = ['classpath:spring/store-rest-test-context.xml'])
class LoginApiTest extends AbstractTestNGSpringContextTests {

    @Autowired(required = true)
    @Qualifier('storeResourceClientProxy')
    private StoreResource storeResource

    @Autowired(required = true)
    @Qualifier('loginResourceClientProxy')
    private LoginResource loginResource

    @Test
    public void testBVT() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String refreshToken = null
        def result = loginResource.checkUserName(new UserNameCheckRequest(username: username, email: email)).get()
        assert result.status == 'SUCCESS'

        String password = Generator.genPassword()
        result = loginResource.rateUserCredential(new UserCredentialRateRequest(
                userCredential: new UserCredential(value: password, type: password)
        )).get()
        assert result.status == 'SUCCESS'

        // create user with invalid password (not enough length)
        def createUserRequest = Generator.genCreateUserRequest(username, '123456', email, pin)
        try {
            result = loginResource.createUser(createUserRequest).get()
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.101'
            // todo verify it's caused by password field
        }

        // create user with invalid password (only characters)
        createUserRequest = Generator.genCreateUserRequest(username, 'abcedfcbda', email, pin)
        try {
            result = loginResource.createUser(createUserRequest).get()
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.001'
            // todo verify it's caused by password field
        }

        createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        result = loginResource.createUser(createUserRequest).get()
        assert result.status == 'SUCCESS'
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
            // todo verify it's caused by invalid user field
        }

        // login with invalid password
        try {
            loginResource.signIn(new UserSignInRequest(username : username, userCredential:  new UserCredential(type: 'PASSWORD', value: "${password}123"))).get()
            assert false
        } catch (AppErrorException ex) {
            // todo verify it's caused by invalid user field
            assert ex != null
        }

        // login
        result = loginResource.signIn(new UserSignInRequest(username: username, userCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
        assert result.username == username
        assert result.accessToken != null
        assert result.refreshToken != null
        assert result.userId == userId

        // check user credential with invalid username
        try {
            loginResource.checkUserCredential(new UserCredentialCheckRequest(username : Generator.genUserName(), userCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.108'
        }

        // check user credential with invalid password
        try {
            loginResource.checkUserCredential(new UserCredentialCheckRequest(username : username, userCredential:  new UserCredential(type: 'PASSWORD', value: "${password}123"))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.109'
        }

        // check user credential with invalid pin
        try {
            loginResource.checkUserCredential(new UserCredentialCheckRequest(username : username, userCredential:  new UserCredential(type: 'PIN', value: "dddd"))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.110'
        }

        // check password success
        result = loginResource.checkUserCredential(new UserCredentialCheckRequest(username : username, userCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
        assert result.status == 'SUCCESS'

        // check pin success
        result = loginResource.checkUserCredential(new UserCredentialCheckRequest(username : username, userCredential:  new UserCredential(type: 'PIN', value: pin))).get()
        assert result.status == 'SUCCESS'

        // get auth token
        result = loginResource.getAuthToken(new AuthTokenRequest(packageName: 'abc', packageSignature: 'abc', refreshToken: refreshToken)).get()
        refreshToken = result.refreshToken
        // assert result.userId == userId
        assert result.username == username
        loginResource.getAuthToken(new AuthTokenRequest(packageName: 'abc', packageSignature: 'abc', refreshToken: refreshToken)).get()

        // get with invalid auth token
        try {
            loginResource.getAuthToken(new AuthTokenRequest(packageName: 'abc', packageSignature: 'abc', refreshToken: refreshToken)).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '132.001'
        }
    }

    @Test
    public void testChangePIN() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        loginResource.createUser(Generator.genCreateUserRequest(username, password, email, pin)).get()

        // invalid user name
        try {
            loginResource.changeUserCredential(new UserCredentialChangeRequest(
                    username : Generator.genUserName(),
                    oldCredential:  new UserCredential(type: 'PIN', value: pin),
                    newCredential:  new UserCredential(type: 'PIN', value: pin))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.108'
        }

        // invalid pin code
        try {
            loginResource.changeUserCredential(new UserCredentialChangeRequest(
                    username : username,
                    oldCredential:  new UserCredential(type: 'PIN', value: Generator.genPIN()),
                    newCredential:  new UserCredential(type: 'PIN', value: pin))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.110'
        }

        // success update
        String newPin = Generator.genPIN()
        def result = loginResource.changeUserCredential(new UserCredentialChangeRequest(
                username : username,
                oldCredential:  new UserCredential(type: 'PIN', value: pin),
                newCredential:  new UserCredential(type: 'PIN', value: newPin))).get()
        assert result.status == 'SUCCESS'

        // check credential
        result = loginResource.checkUserCredential(new UserCredentialCheckRequest(username: username, userCredential:  new UserCredential(type: 'PIN', value: newPin))).get()
        assert result.status == 'SUCCESS'
    }

    @Test
    public void testChangePassword() {

        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()

        loginResource.createUser(Generator.genCreateUserRequest(username, password, email, pin)).get()

        // invalid user name
        try {
            loginResource.changeUserCredential(new UserCredentialChangeRequest(
                    username : Generator.genUserName(),
                    oldCredential:  new UserCredential(type: 'PASSWORD', value: password),
                    newCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.108'
        }

        // invalid password code
        try {
            loginResource.changeUserCredential(new UserCredentialChangeRequest(
                    username : username,
                    oldCredential:  new UserCredential(type: 'PASSWORD', value: pin),
                    newCredential:  new UserCredential(type: 'PASSWORD', value: password))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '131.109'
        }

        // success update
        String newPassword = Generator.genPassword()
        def result = loginResource.changeUserCredential(new UserCredentialChangeRequest(
                username : username,
                oldCredential:  new UserCredential(type: 'PASSWORD', value: password),
                newCredential:  new UserCredential(type: 'PASSWORD', value: newPassword))).get()
        assert result.status == 'SUCCESS'

        // check credential
        result = loginResource.checkUserCredential(new UserCredentialCheckRequest(username: username, userCredential:  new UserCredential(type: 'PASSWORD', value: newPassword))).get()
        assert result.status == 'SUCCESS'
    }
}
