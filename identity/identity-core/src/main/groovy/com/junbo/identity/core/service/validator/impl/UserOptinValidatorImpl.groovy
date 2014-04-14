package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserOptinId
import com.junbo.identity.core.service.validator.UserOptinValidator
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserOptin
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserOptinValidatorImpl implements UserOptinValidator {

    private UserRepository userRepository

    private UserOptinRepository userOptinRepository

    private List<String> allowedTypes

    @Override
    Promise<UserOptin> validateForGet(UserOptinId userOptinId) {

        if (userOptinId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userOptinId').exception()
        }

        return userOptinRepository.get(userOptinId).then { UserOptin userOptin ->
            if (userOptin == null) {
                throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
            }

            return Promise.pure(userOptin)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserOptinListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.type == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or type').exception()
        }

        if (options.userId != null && options.type != null) {
            throw AppErrors.INSTANCE.parameterInvalid('userId and type can\'t set at the same time').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserOptin userOptin) {
        checkBasicUserOptinInfo(userOptin)
        if (userOptin.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return userOptinRepository.search(new UserOptinListOptions(
                userId: userOptin.userId,
                type: userOptin.type
        )).then { List<UserOptin> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('type').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserOptinId userOptinId, UserOptin userOptin, UserOptin oldUserOptin) {

        return validateForGet(userOptinId).then { UserOptin existingUserOptin ->
            checkBasicUserOptinInfo(userOptin)

            if (existingUserOptin.userId != userOptin.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (existingUserOptin.userId != oldUserOptin.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (userOptin.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userOptin.id != userOptinId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userOptinId.value.toString()).exception()
            }

            if (userOptin.id != oldUserOptin.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserOptin.id.toString()).exception()
            }

            if (userOptin.type != oldUserOptin.type) {
                return userOptinRepository.search(new UserOptinListOptions(
                        userId: userOptin.userId,
                        type: userOptin.type
                )).then { List<UserOptin> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('type').exception()
                    }

                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserOptinRepository(UserOptinRepository userOptinRepository) {
        this.userOptinRepository = userOptinRepository
    }

    @Required
    void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes
    }

    private void checkBasicUserOptinInfo(UserOptin userOptin) {
        if (userOptin == null) {
            throw new IllegalArgumentException('userOptin is null')
        }

        if (userOptin.type == null) {
            throw new IllegalArgumentException('type is null')
        }

        if (!(userOptin.type in allowedTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
        }

        if (userOptin.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        userRepository.get(userOptin.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userOptin.userId).exception()
            }

            if (existingUser.active == null || existingUser.active == false) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userOptin.userId).exception()
            }
        }
    }
}
