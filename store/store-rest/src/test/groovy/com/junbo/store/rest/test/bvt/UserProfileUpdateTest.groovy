package com.junbo.store.rest.test.bvt

import com.junbo.common.error.AppErrorException
import com.junbo.store.rest.test.Generator
import com.junbo.store.spec.model.ChallengeAnswer
import com.junbo.store.spec.model.identity.StoreUserProfile
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import org.testng.annotations.Test

/**
 * The TestBase class
 */
class UserProfileUpdateTest extends TestBase {

    @Test
    public void testAddUpdatePassword() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()
        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.setToken(result.accessToken)

        String newPassword = Generator.genPassword()
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword))).get()
        assert result.challenge.type == 'PASSWORD'
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword),
                challengeAnswer: new ChallengeAnswer(type: 'PIN'))).get()
        assert result.challenge.type == 'PASSWORD'
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword),
                challengeAnswer: new ChallengeAnswer(type: 'PASSWORD'))).get()
        assert result.challenge.type == 'PASSWORD'

        try {
            storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword),
                    challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: Generator.genPassword()))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.108'
        }

        // change the password
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : newPassword),
                challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: password))).get()
        assert result != null
        // verify it's changed by change it again
        storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(password : Generator.genPassword()),
                challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: newPassword))).get()
        assert result != null
    }

    @Test
    public void testAddUpdatePIN() {
        String username = Generator.genUserName()
        String email = Generator.genEmail()
        String pin = Generator.genPIN()
        String password = Generator.genPassword()
        def createUserRequest = Generator.genCreateUserRequest(username, password, email, pin)
        def result = loginResource.createUser(createUserRequest).get()
        testAccessTokenProvider.setToken(result.accessToken)

        String newPIN = Generator.genPIN()
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(pin : newPIN))).get()
        assert result.challenge.type == 'PASSWORD'
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(pin : newPIN),
                challengeAnswer: new ChallengeAnswer(type: 'PIN'))).get()
        assert result.challenge.type == 'PASSWORD'
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(pin : newPIN),
                challengeAnswer: new ChallengeAnswer(type: 'PASSWORD'))).get()
        assert result.challenge.type == 'PASSWORD'

        try {
            storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(pin : newPIN),
                    challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: Generator.genPassword()))).get()
            assert false
        } catch (AppErrorException ex) {
            assert ex.error.error().code == '130.108'
        }

        // change the password
        result = storeResource.updateUserProfile(new UserProfileUpdateRequest(userProfile: new StoreUserProfile(pin : newPIN),
                challengeAnswer: new ChallengeAnswer(type: 'PASSWORD', password: password))).get()
        assert result != null
    }
}
