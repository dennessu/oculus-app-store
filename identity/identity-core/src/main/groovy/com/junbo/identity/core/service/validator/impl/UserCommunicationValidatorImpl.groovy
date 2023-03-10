package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.validator.UserCommunicationValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.service.CommunicationService
import com.junbo.identity.service.UserCommunicationService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Check user valid (not anonymous user, non-active user)
 * Check communication exists
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserCommunicationValidatorImpl implements UserCommunicationValidator {

    private UserService userService
    private UserCommunicationService userCommunicationService
    private CommunicationService communicationService

    @Override
    Promise<UserCommunication> validateForGet(UserCommunicationId userCommunicationId) {

        if (userCommunicationId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userCommunicationId').exception()
        }

        return userCommunicationService.get(userCommunicationId).then { UserCommunication userCommunication ->
            if (userCommunication == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userCommunicationId).exception()
            }

            return Promise.pure(userCommunication)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserOptinListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserCommunication userCommunication) {
        return checkBasicUserCommunicationInfo(userCommunication).then {
            if (userCommunication.id != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
            }

            return userCommunicationService.searchByUserIdAndCommunicationId(userCommunication.userId,
                    userCommunication.communicationId, 1, 0).then { Results<UserCommunication> existing ->
                if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('communicationId').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserCommunicationId userCommunicationId, UserCommunication userCommunication,
                                    UserCommunication oldUserCommunication) {

        return validateForGet(userCommunicationId).then { UserCommunication existingUserOptin ->
            if (existingUserOptin.userId != userCommunication.userId) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (existingUserOptin.userId != oldUserCommunication.userId) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            return checkBasicUserCommunicationInfo(userCommunication)
        }.then {
            if (userCommunication.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userCommunication.id != userCommunicationId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userCommunication.id, userCommunicationId).exception()
            }

            if (userCommunication.id != oldUserCommunication.id) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userCommunication.id, oldUserCommunication.id).exception()
            }

            if (userCommunication.communicationId != oldUserCommunication.communicationId) {
                return userCommunicationService.searchByUserIdAndCommunicationId(userCommunication.userId,
                        userCommunication.communicationId, 1, 0).then { Results<UserCommunication> existing ->
                    if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('communicationId').exception()
                    }

                    return Promise.pure(null)
                }
            }
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicUserCommunicationInfo(UserCommunication userCommunication) {
        if (userCommunication == null) {
            throw new IllegalArgumentException('userCommunication is null')
        }

        if (userCommunication.communicationId == null) {
            throw new IllegalArgumentException('communicationId is null')
        }

        if (userCommunication.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userService.getNonDeletedUser(userCommunication.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userCommunication.userId).exception()
            }

            if (existingUser.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userCommunication.userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userCommunication.userId).exception()
            }

            return communicationService.get(userCommunication.communicationId).then { Communication communication ->
                if (communication == null) {
                    throw AppErrors.INSTANCE.communicationNotFound(userCommunication.communicationId).exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setUserCommunicationService(UserCommunicationService userCommunicationService) {
        this.userCommunicationService = userCommunicationService
    }

    @Required
    void setCommunicationService(CommunicationService communicationService) {
        this.communicationService = communicationService
    }
}
