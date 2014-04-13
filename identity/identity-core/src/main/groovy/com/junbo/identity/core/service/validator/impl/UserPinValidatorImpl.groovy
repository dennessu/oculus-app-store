package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserPinValidator
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPinValidatorImpl implements UserPinValidator {

    private UserRepository userRepository

    private UserPinRepository userPinRepository

    private Integer valueMinLength
    private Integer valueMaxLength

    @Override
    Promise<UserPin> validateForGet(UserId userId, UserPinId userPinId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userPinId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userPinId').exception()
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
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userPinId doesn\'t match.').exception()
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
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
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
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userPin.userId != null && userPin.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userPin.userId.toString()).exception()
        }

        if (userPin.active != null) {
            throw AppErrors.INSTANCE.fieldInvalid('active').exception()
        }

        userPin.setPinSalt(UUID.randomUUID().toString())
        userPin.setPinHash(CipherHelper.hashPassword(userPin.value, userPin.pinSalt))
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

        String decoded = Base64.decodeAsString(oldPassword)
        String[] split = decoded.split(':')
        String decryptPassword = split[1]

        userPinRepository.search(new UserPinListOptions(
                userId: userId,
                active: true
        )).then { List<UserPin> userPinList ->
            if (userPinList == null || userPinList.size() == 0 || userPinList.size() > 1) {
                throw AppErrors.INSTANCE.userPinIncorrect().exception()
            }

            if (CipherHelper.hashPassword(decryptPassword, userPinList.get(0).pinSalt) != userPinList.get(0).pinHash) {
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

        if (userPin.value.size() > valueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', valueMaxLength).exception()
        }

        if (userPin.value.size() < valueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', valueMinLength).exception()
        }

        if (userPin.expiresBy != null) {
            if (userPin.expiresBy.before(new Date())) {
                throw AppErrors.INSTANCE.fieldInvalid(userPin.expiresBy.toString()).exception()
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
    void setValueMinLength(Integer valueMinLength) {
        this.valueMinLength = valueMinLength
    }

    @Required
    void setValueMaxLength(Integer valueMaxLength) {
        this.valueMaxLength = valueMaxLength
    }
}
