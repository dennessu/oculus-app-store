package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserCommunicationId
import com.junbo.identity.core.service.validator.UserCommunicationValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.data.repository.UserRepository
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
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserCommunicationValidatorImpl implements UserCommunicationValidator {

    private UserRepository userRepository
    private UserCommunicationRepository userCommunicationRepository
    private CommunicationRepository communicationRepository

    @Override
    Promise<UserCommunication> validateForGet(UserCommunicationId userCommunicationId) {

        if (userCommunicationId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userCommunicationId').exception()
        }

        return userCommunicationRepository.get(userCommunicationId).then { UserCommunication userCommunication ->
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

        if (options.userId == null && options.communicationId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or communicationId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserCommunication userCommunication) {
        return checkBasicUserCommunicationInfo(userCommunication).then {
            if (userCommunication.id != null) {
                throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
            }

            return userCommunicationRepository.search(new UserOptinListOptions(
                    userId: userCommunication.userId,
                    communicationId: userCommunication.communicationId
            )).then { List<UserCommunication> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppErrors.INSTANCE.fieldDuplicate('communicationId').exception()
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
                throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (existingUserOptin.userId != oldUserCommunication.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            return checkBasicUserCommunicationInfo(userCommunication)
        }.then {
            if (userCommunication.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userCommunication.id != userCommunicationId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userCommunicationId.value.toString()).exception()
            }

            if (userCommunication.id != oldUserCommunication.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserCommunication.id.toString()).exception()
            }

            if (userCommunication.communicationId != oldUserCommunication.communicationId) {
                return userCommunicationRepository.search(new UserOptinListOptions(
                        userId: userCommunication.userId,
                        communicationId: userCommunication.communicationId
                )).then { List<UserCommunication> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('communicationId').exception()
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
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userRepository.get(userCommunication.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userCommunication.userId).exception()
            }

            if (existingUser.isAnonymous == true) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userCommunication.userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userCommunication.userId).exception()
            }

            return communicationRepository.get(userCommunication.communicationId).then { Communication communication ->
                if (communication == null) {
                    throw AppErrors.INSTANCE.communicationNotFound(userCommunication.communicationId).exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserCommunicationRepository(UserCommunicationRepository userCommunicationRepository) {
        this.userCommunicationRepository = userCommunicationRepository
    }

    @Required
    void setCommunicationRepository(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository
    }
}
