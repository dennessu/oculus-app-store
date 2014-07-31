package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.core.service.util.CodeGenerator
import com.junbo.identity.core.service.validator.UserTFABackupCodeValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/5/14.
 */
@CompileStatic
class UserTFABackupCodeValidatorImpl implements UserTFABackupCodeValidator {

    private UserTFAPhoneBackupCodeRepository userTFABackupCodeRepository
    private UserRepository userRepository
    private CodeGenerator codeGenerator

    @Override
    Promise<UserTFABackupCode> validateForGet(UserId userId, UserTFABackupCodeId userTFABackupCodeId) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFABackupCodeId == null) {
            throw new IllegalArgumentException('userTFABackupCodeId is null')
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }
            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTFABackupCodeRepository.get(userTFABackupCodeId).then {
                UserTFABackupCode userTFABackupCode ->
                    if (userTFABackupCode == null) {
                        throw AppErrors.INSTANCE.userTFABackupCodeIncorrect().exception()
                    }

                    if (userTFABackupCode.userId != userId) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('userId',
                                'userId and userTFABackupCodeId doesn\'t match.').exception()
                    }

                    return Promise.pure(userTFABackupCode)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserId userId, UserTFABackupCodeListOptions options) {
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
    Promise<Void> validateForCreate(UserId userId, UserTFABackupCode userTFABackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (userTFABackupCode == null) {
            throw new IllegalArgumentException('userTFABackupCode is null')
        }

        if (userTFABackupCode.userId != null && userTFABackupCode.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFABackupCode.userId, userId).exception()
        }
        userTFABackupCode.userId = userId

        if (userTFABackupCode.expiresBy == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('expiresBy').exception()
        }
        if (userTFABackupCode.expiresBy.before(new Date())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('expiresBy').exception()
        }

        if (userTFABackupCode.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        if (userTFABackupCode.active != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('active').exception()
        }

        userTFABackupCode.active = true

        if (userTFABackupCode.verifyCode != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('verifyCode').exception()
        }

        userTFABackupCode.verifyCode = codeGenerator.generateCode()

        return checkBasicUserTFABackupCode(userTFABackupCode)
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTFABackupCodeId userTFABackupCodeId,
                                    UserTFABackupCode userTFABackupCode, UserTFABackupCode oldUserTFABackupCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTFABackupCodeId == null) {
            throw new IllegalArgumentException('userTFABackupCodeId is null')
        }

        if (userTFABackupCode.userId != null && userTFABackupCode.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userTFABackupCode.userId, userId).exception()
        }

        if (oldUserTFABackupCode.userId != null && oldUserTFABackupCode.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userId, oldUserTFABackupCode.userId).exception()
        }

        if (userTFABackupCodeId != userTFABackupCode.id) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userTFABackupCodeId', userTFABackupCode.id, userTFABackupCodeId).exception()
        }

        if (userTFABackupCodeId != oldUserTFABackupCode.id) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userTFABackupCodeId', userTFABackupCodeId, oldUserTFABackupCode.id).exception()
        }

        if (userTFABackupCode.id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }

        if (userTFABackupCode.expiresBy == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('expiresBy').exception()
        }
        if (userTFABackupCode.active == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('active').exception()
        }

        if (userTFABackupCode.verifyCode != null
         && userTFABackupCode.verifyCode != oldUserTFABackupCode.verifyCode) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('verifyCode', userTFABackupCode.verifyCode, oldUserTFABackupCode.verifyCode).exception()
        }

        return checkBasicUserTFABackupCode(userTFABackupCode)
    }

    Promise<Void> checkBasicUserTFABackupCode(UserTFABackupCode userTFABackupCode) {

        return userRepository.get(userTFABackupCode.userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userTFABackupCode.userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userTFABackupCode.userId).exception()
            }
            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userTFABackupCode.userId).exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserTFABackupCodeRepository(UserTFAPhoneBackupCodeRepository userTFABackupCodeRepository) {
        this.userTFABackupCodeRepository = userTFABackupCodeRepository
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
