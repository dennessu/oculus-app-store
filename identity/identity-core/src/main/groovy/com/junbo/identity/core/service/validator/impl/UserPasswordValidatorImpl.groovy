package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.core.service.util.UserPasswordUtil
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.options.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPasswordValidatorImpl implements UserPasswordValidator {

    private UserRepository userRepository

    private UserPasswordRepository userPasswordRepository

    @Override
    Promise<UserPassword> validateForGet(UserId userId, UserPasswordId userPasswordId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userPasswordId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userPasswordId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userPasswordRepository.get(userPasswordId).then { UserPassword userPassword ->
                if (userPassword == null) {
                    throw AppErrors.INSTANCE.userPasswordNotFound(userPasswordId).exception()
                }

                if (userId != userPassword.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userPasswordId doesn\'t match.').exception()
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
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
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
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userPassword.userId != null && userPassword.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userPassword.userId.toString()).exception()
        }

        if (userPassword.active != null) {
            throw AppErrors.INSTANCE.fieldInvalid('active').exception()
        }

        userPassword.setPasswordSalt(UUID.randomUUID().toString())
        userPassword.setStrength(UserPasswordUtil.calcPwdStrength(userPassword.value))
        userPassword.setPasswordHash(UserPasswordUtil.hashPassword(userPassword.value, userPassword.passwordSalt))
        userPassword.setUserId(userId)
        userPassword.setActive(true)

        return Promise.pure(null)
    }

    private void checkBasicUserPasswordInfo(UserPassword userPassword) {
        if (userPassword == null) {
            throw new IllegalArgumentException('userPassword is null')
        }

        if (userPassword.value == null) {
            throw new IllegalArgumentException('value is null')
        }

        UserPasswordUtil.validatePassword(userPassword.value)

        if (userPassword.expiresBy != null) {
            if (userPassword.expiresBy.before(new Date())) {
                throw AppErrors.INSTANCE.fieldInvalid(userPassword.expiresBy.toString()).exception()
            }
        }

        if (userPassword.strength != null) {
            String strength = UserPasswordUtil.calcPwdStrength(userPassword.value)
            if (strength == userPassword.strength) {
                throw AppErrors.INSTANCE.fieldInvalid(userPassword.value).exception()
            }
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserPasswordRepository(UserPasswordRepository userPasswordRepository) {
        this.userPasswordRepository = userPasswordRepository
    }
}
