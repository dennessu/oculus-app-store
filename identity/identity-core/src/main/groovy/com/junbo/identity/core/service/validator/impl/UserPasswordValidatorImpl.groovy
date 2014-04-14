package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.core.service.util.CipherHelper
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserPasswordValidatorImpl implements UserPasswordValidator {
    private static final Integer SALT_LENGTH = 20

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

        userPassword.setPasswordSalt(CipherHelper.generateCipherRandomStr(SALT_LENGTH))
        userPassword.setPasswordPepper(CipherHelper.generateCipherRandomStr(SALT_LENGTH))
        userPassword.setStrength(CipherHelper.calcPwdStrength(userPassword.value))
        userPassword.setPasswordHash(CipherHelper.generateCipherHashV1(userPassword.value,
                userPassword.passwordSalt, userPassword.passwordPepper))
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

        String decoded = Base64.decodeAsString(oldPassword)
        String[] split = decoded.split(':')
        String decryptPassword = split[1]

        userPasswordRepository.search(new UserPasswordListOptions(
                userId: userId,
                active: true
        )).then { List<UserPassword> userPasswordList ->
            if (userPasswordList == null || userPasswordList.size() == 0 || userPasswordList.size() > 1) {
                throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
            }

            if (CipherHelper.generateCipherHashV1(decryptPassword, userPasswordList.get(0).passwordSalt,
                    userPasswordList.get(0).passwordPepper) != userPasswordList.get(0).passwordHash) {
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
                throw AppErrors.INSTANCE.fieldInvalid(userPassword.expiresBy.toString()).exception()
            }
        }

        if (userPassword.strength != null) {
            String strength = CipherHelper.calcPwdStrength(userPassword.value)
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
