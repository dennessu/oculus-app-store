package com.junbo.store.clientproxy.error

import com.junbo.store.spec.error.AppErrors
import groovy.transform.CompileStatic

/**
 * The ErrorCodeConstants.
 */
@CompileStatic
class ErrorCodes {

    class Identity {
        static final String majorCode = '131'
        static final String FieldDuplicate = '131.002'
        static final String UserPasswordIncorrect = '131.109'
        static final String CountryNotFound = '131.122'
        static final String LocaleNotFound = '131.124'
        static final String InvalidPassword = '131.101'
        static final String InvalidPin = '131.110'
        static final String InvalidField = '131.001'
        static final String MaximumLoginAttempt = '131.139'
        static final String AgeRestriction = '131.140'
        static final String TosNotFound = '131.119'
    }

    class OAuth {
        static final String InvalidCredential = '132.103'
        static final String RefreshTokenInvalid = '132.001'
        static final String RefreshTokenExpired = '132.104'
        static final String SentryLoginError = '132.144'
    }

    class Catalog {
        static final String ResourceNotFound = '123.004'
    }

    class Store {
        static final String majorCode = '130'
        static final String UnknownError = AppErrors.INSTANCE.unknownError().error().code
    }

    class Sentry {
        static final String BlockAccess = '130.123'
    }

    class Casey {
        static final String ReviewCreateError = '104.105'
        static final String ReviewUpdateError = '104.106'
        static final String LinkedResourceValidationFailed = '101.104'
    }
}
