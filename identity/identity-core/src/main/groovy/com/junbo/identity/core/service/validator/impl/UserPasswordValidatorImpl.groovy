package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.credential.CredentialHelper
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.service.UserPasswordService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Check user valid
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserPasswordValidatorImpl implements UserPasswordValidator {
    private UserService userService

    private UserPasswordService userPasswordService

    private Integer currentCredentialVersion

    private CredentialHashFactory credentialHashFactory

    private CredentialHelper credentialHelper

    @Override
    Promise<UserPassword> validateForGet(UserId userId, UserPasswordId userPasswordId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userPasswordId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userPasswordId').exception()
        }

        return userService.getNonDeletedUser(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userPasswordService.get(userPasswordId).then { UserPassword userPassword ->
                if (userPassword == null) {
                    throw AppErrors.INSTANCE.userPasswordNotFound(userPasswordId).exception()
                }

                if (userId != userPassword.userId) {
                    throw AppCommonErrors.INSTANCE.parameterInvalid('userId', 'userId and userPassword.userId doesn\'t match.').exception()
                }

                return Promise.pure(userPassword)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPasswordListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserPassword userPassword) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserPasswordInfo(userPassword)
        if (userPassword.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }
        if (userPassword.userId != null && userPassword.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userPassword.userId, userId).exception()
        }

        if (userPassword.active != null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('active').exception()
        }

        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
        CredentialHash matched = credentialHashList.find { CredentialHash credentialHash ->
            return credentialHash.handles(currentCredentialVersion)
        }
        if (matched == null) {
            throw new IllegalStateException('No matched version: ' + currentCredentialVersion + ' for CredentialHash')
        }

        userPassword.setStrength(CipherHelper.calcPwdStrength(userPassword.value))
        if (userPassword.value.length() < 8) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'Password must be equals or larger than 8 characters').exception()
        }
        userPassword.setPasswordHash(matched.hash(userPassword.value))
        userPassword.setUserId(userId)
        userPassword.setActive(true)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForOldPassword(UserId userId, String oldPassword) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (StringUtils.isEmpty(oldPassword)) {
            return Promise.pure(null)
        }

        return credentialHelper.isValidPassword(userId, oldPassword).then { Boolean isValid ->
            if (!isValid) {
                throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
            }

            return Promise.pure(null)
        }
    }

    private void checkBasicUserPasswordInfo(UserPassword userPassword) {
        if (userPassword == null) {
            throw new IllegalArgumentException('userPassword is null')
        }

        if (userPassword.value == null) {
            throw new IllegalArgumentException('value is null')
        }

        CipherHelper.validatePassword(userPassword.value)

        if (userPassword.expiresBy != null) {
            if (userPassword.expiresBy.before(new Date())) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(userPassword.expiresBy.toString()).exception()
            }
        }

        if (userPassword.strength != null) {
            String strength = CipherHelper.calcPwdStrength(userPassword.value)
            if (strength != userPassword.strength) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('strength', userPassword.strength, strength).exception()
            }
        }
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setUserPasswordService(UserPasswordService userPasswordService) {
        this.userPasswordService = userPasswordService
    }

    @Required
    void setCurrentCredentialVersion(Integer currentCredentialVersion) {
        this.currentCredentialVersion = currentCredentialVersion
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setCredentialHelper(CredentialHelper credentialHelper) {
        this.credentialHelper = credentialHelper
    }
}
