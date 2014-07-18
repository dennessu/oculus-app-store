package com.junbo.store.rest.utils

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.login.AuthTokenRequest
import com.junbo.store.spec.model.login.CreateUserRequest
import com.junbo.store.spec.model.login.UserCredentialCheckRequest
import com.junbo.store.spec.model.login.UserNameCheckRequest
import com.junbo.store.spec.model.login.UserSignInRequest
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * The RequestValidator class.
 */
@CompileStatic
@Component('storeRequestValidator')
class RequestValidator {

    Promise<Void> validateUserNameCheckRequest(UserNameCheckRequest request) {
        if (StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('username').exception()
        }
        if (StringUtils.isEmpty(request.email)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email').exception()
        }
        return Promise.pure(null)
    }

    Promise<Void> validateCreateUserRequest(CreateUserRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.email, 'email')
        notEmpty(request.password, 'password')
        notEmpty(request.pinCode, 'pinCode')
        notEmpty(request.dob, 'dob')
        notEmpty(request.cor, 'cor')
        notEmpty(request.preferredLocale, 'preferredLocale')
        notEmpty(request.firstName, 'firstName')
        notEmpty(request.lastName, 'lastName')
        return Promise.pure(null)
    }

    Promise<Void> validateUserSignInRequest(UserSignInRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
        if (!'PASSWORD'.equalsIgnoreCase(request.userCredential.type)) { //
            throw AppCommonErrors.INSTANCE.fieldInvalid('userCredential.type', 'type must be PASSWORD ').exception()
        }
        return Promise.pure(null)
    }

    Promise<Void> validateUserCredentialCheckRequest(UserCredentialCheckRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
        return Promise.pure(null)
    }

    Promise<Void> validateAuthTokenRequest(AuthTokenRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.refreshToken, 'refreshToken')
        return Promise.pure(null)
    }

    private static void notEmpty(Object val, String fieldName) {
        if (StringUtils.isEmpty(val)) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
    }
}
