package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.core.service.util.CodeGenerator
import com.junbo.identity.core.service.validator.UserTeleBackupCodeValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleBackupCodeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTeleBackupCodeValidatorImpl implements UserTeleBackupCodeValidator {

    private UserTeleBackupCodeRepository userTeleBackupCodeRepository
    private UserRepository userRepository
    private CodeGenerator codeGenerator

    @Override
    Promise<UserTeleBackupCode> validateForGet(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleBackupCodeId == null) {
            throw new IllegalArgumentException('userTeleBackupCodeId is null')
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }
            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            if (user.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTeleBackupCodeRepository.get(userTeleBackupCodeId).then {
                UserTeleBackupCode userTeleBackupCode ->
                    if (userTeleBackupCode == null) {
                        throw AppErrors.INSTANCE.userTeleBackupCodeIncorrect().exception()
                    }

                    if (userTeleBackupCode.userId != userId) {
                        throw AppErrors.INSTANCE.fieldInvalidException('userId',
                                'userId and userTeleBackupCodeId doesn\'t match.').exception()
                    }

                    return Promise.pure(userTeleBackupCode)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserId userId, UserTeleBackupCodeListOptions options) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        options.userId = userId
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTeleBackupCode userTeleBackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (userTeleBackupCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        if (userTeleBackupCode.userId != null && userTeleBackupCode.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }
        userTeleBackupCode.userId = userId

        if (userTeleBackupCode.expiresBy == null) {
            throw AppErrors.INSTANCE.fieldRequired('expiresBy').exception()
        }
        if (userTeleBackupCode.expiresBy.before(new Date())) {
            throw AppErrors.INSTANCE.fieldInvalid('expiresBy').exception()
        }

        if (userTeleBackupCode.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userTeleBackupCode.active != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('active').exception()
        }

        userTeleBackupCode.active = true

        if (userTeleBackupCode.verifyCode != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        userTeleBackupCode.verifyCode = codeGenerator.generateTeleBackupCode()

        return checkBasicUserTeleBackupCode(userTeleBackupCode)
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTeleBackupCodeId userTeleBackupCodeId,
                                    UserTeleBackupCode userTeleBackupCode, UserTeleBackupCode oldUserTeleBackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleBackupCodeId == null) {
            throw new IllegalArgumentException('userTeleBackupCodeId is null')
        }

        if (userTeleBackupCode.userId != null && userTeleBackupCode.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        if (oldUserTeleBackupCode.userId != null && oldUserTeleBackupCode.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        if (userTeleBackupCodeId != userTeleBackupCode.id) {
            throw AppErrors.INSTANCE.fieldInvalid('userTeleBackupCodeId', userTeleBackupCodeId.toString()).exception()
        }

        if (userTeleBackupCodeId != oldUserTeleBackupCode.id) {
            throw AppErrors.INSTANCE.fieldInvalid('oldUserTeleBackupCode', userTeleBackupCodeId.toString()).exception()
        }

        if (userTeleBackupCode.id == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }

        if (userTeleBackupCode.expiresBy == null) {
            throw AppErrors.INSTANCE.fieldRequired('expiresBy').exception()
        }
        if (userTeleBackupCode.active == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }

        if (userTeleBackupCode.verifyCode != null
         && userTeleBackupCode.verifyCode != oldUserTeleBackupCode.verifyCode) {
            throw AppErrors.INSTANCE.fieldNotWritable('verifyCode').exception()
        }

        return checkBasicUserTeleBackupCode(userTeleBackupCode)
    }

    Promise<Void> checkBasicUserTeleBackupCode(UserTeleBackupCode userTeleBackupCode) {

        return userRepository.get(userTeleBackupCode.userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userTeleBackupCode.userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userTeleBackupCode.userId).exception()
            }
            if (user.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userTeleBackupCode.userId).exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserTeleBackupCodeRepository(UserTeleBackupCodeRepository userTeleBackupCodeRepository) {
        this.userTeleBackupCodeRepository = userTeleBackupCodeRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setCodeGenerator(CodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator
    }
}
