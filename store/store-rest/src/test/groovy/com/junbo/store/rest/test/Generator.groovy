package com.junbo.store.rest.test

import com.junbo.store.spec.model.login.CreateUserRequest
import org.apache.commons.lang.RandomStringUtils

/**
 * The Generator class.
 */
class Generator {

    static String genUserName() {
        return RandomStringUtils.randomAlphabetic(20)
    }

    static String genPassword() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase() + RandomStringUtils.randomAlphabetic(2).toUpperCase() + RandomStringUtils.randomNumeric(1) + '$'
    }

    static String genEmail() {
        return "${RandomStringUtils.randomAlphabetic(20)}@test.com"
    }

    static String genPIN() {
        return RandomStringUtils.randomNumeric(4)
    }

    static CreateUserRequest genCreateUserRequest(String username, String password, String email, String pinCode) {
        return new CreateUserRequest(
                username: username,
                firstName: 'First',
                lastName: 'Last',
                password: password,
                pinCode: pinCode,
                dob: new Date(System.currentTimeMillis() - 8 * 365L * 24 * 3600 * 1000),
                preferredLocale: 'en_US',
                email: email,
                cor: 'US'
        )
    }
}
