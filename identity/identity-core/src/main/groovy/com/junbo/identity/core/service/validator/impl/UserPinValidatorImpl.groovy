package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.validator.UserPinValidator
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Check user's pin minimum and maximum length
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserPinValidatorImpl implements UserPinValidator {
    private UserRepository userRepository

    private UserPinRepository userPinRepository

    private CredentialHashFactory credentialHashFactory

    private Integer currentCredentialVersion

    private Pattern allowedPattern

    @Override
    Promise<UserPin> validateForGet(UserId userId, UserPinId userPinId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userPinId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userPinId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userPinRepository.get(userPinId).then { UserPin userPin ->
                if (userPin == null) {
                    throw AppErrors.INSTANCE.userPinNotFound(userPinId).exception()
                }

                if (userId != userPin.userId) {
                    throw AppCommonErrors.INSTANCE.parameterInvalid('userId', 'userId and userPin.userId doesn\'t match.').exception()
                }

                return Promise.pure(userPin)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPinListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserPin userPin) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserPinInfo(userPin)
        if (userPin.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }
        if (userPin.userId != null && userPin.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userPin.userId, userId).exception()
        }

        if (userPin.active != null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('active').exception()
        }

        List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
        CredentialHash matched = credentialHashList.find { CredentialHash hash ->
            return hash.handles(currentCredentialVersion)
        }

        if (matched == null) {
            throw new IllegalStateException('No matched version: ' + currentCredentialVersion + ' for CredentialHash')
        }

        userPin.setPinHash(matched.hash(userPin.value))
        userPin.setUserId(userId)
        userPin.setActive(true)

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

        return userPinRepository.searchByUserIdAndActiveStatus(userId, true, Integer.MAX_VALUE,
                0).then { List<UserPin> userPinList ->
            if (userPinList == null || userPinList.size() == 0 || userPinList.size() > 1) {
                throw AppErrors.INSTANCE.userPinIncorrect().exception()
            }

            List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
            CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                return hash.matches(oldPassword, userPinList.get(0).pinHash)
            }

            if (matched == null) {
                throw AppErrors.INSTANCE.userPinIncorrect().exception()
            }
            return Promise.pure(null)
        }
    }

    private void checkBasicUserPinInfo(UserPin userPin) {
        if (userPin == null) {
            throw new IllegalArgumentException('userPin is null')
        }

        if (userPin.value == null) {
            throw new IllegalArgumentException('value is null')
        }

        if (!allowedPattern.matcher(userPin.value).matches()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('vale', 'Invalid Pin Pattern').exception()
        }

        if (userPin.expiresBy != null) {
            if (userPin.expiresBy.before(new Date())) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(userPin.expiresBy.toString()).exception()
            }
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserPinRepository(UserPinRepository userPinRepository) {
        this.userPinRepository = userPinRepository
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setCurrentCredentialVersion(Integer currentCredentialVersion) {
        this.currentCredentialVersion = currentCredentialVersion
    }

    @Required
    void setAllowedPattern(String allowedPattern) {
        this.allowedPattern = Pattern.compile(allowedPattern)
    }
}
