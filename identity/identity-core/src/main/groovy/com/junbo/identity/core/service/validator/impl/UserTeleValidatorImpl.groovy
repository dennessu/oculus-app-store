package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.core.service.validator.UserTeleValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTeleValidatorImpl implements UserTeleValidator {

    private UserRepository userRepository
    private UserTeleRepository userTeleRepository

    @Override
    Promise<UserTeleCode> validateForGet(UserId userId, UserTeleId userTeleId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTeleId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userTeleId').exception()
        }

        return userRepository.get(userId).then { User existing ->
            if (existing == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existing.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existing.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userTeleRepository.get(userTeleId).then { UserTeleCode existingUserTeleCode ->
                if (existingUserTeleCode == null) {
                    throw AppErrors.INSTANCE.userTeleCodeNotFound(userTeleId).exception()
                }

                if (existingUserTeleCode.userId != userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userTeleCodeId and userId doesn\'t match.').exception()
                }

                return Promise.pure(existingUserTeleCode)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTeleListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTeleCode userTeleCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTeleCode == null) {
            throw new IllegalArgumentException('userTeleCode is null')
        }

        return userRepository.get(userId).then { User existing ->
            if (existing == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (existing.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (existing.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTeleId userTeleId, UserTeleCode userTeleCode, UserTeleCode oldUserTeleCode) {
        return null
    }
}
