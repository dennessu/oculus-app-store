package com.junbo.store.rest.utils

import com.junbo.store.spec.error.AppErrors
import groovy.transform.CompileStatic

/**
 * The ErrorCodeConstants.
 */
@CompileStatic
class ErrorCodes {

    class Identity {
        static final String FieldDuplicate = '131.002'
        static final String UserPasswordIncorrect = '131.109'
        static final String CountryNotFound = '131.122'
        static final String LocaleNotFound = '131.124'
        static final String InvalidPassword = '131.101'
    }

    class OAuth {
        static final String InvalidCredential = '132.103'
        static final String RefreshTokenInvalid = '132.001'
        static final String RefreshTokenExpired = '132.104 '
    }

    class Store {
        static final String UnknownError = AppErrors.INSTANCE.unknownError().error().code
    }
}
