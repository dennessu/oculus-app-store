package com.junbo.identity.core.service.validator.impl
import com.junbo.common.id.UserId
import com.junbo.common.id.UserOptinId
import com.junbo.identity.core.service.validator.UserOptinValidator
import com.junbo.identity.data.repository.UserOptinRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.identity.spec.options.list.UserOptinListOptions
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
    Promise<UserOptin> validateForGet(UserId userId, UserOptinId userOptinId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userOptinId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userOptinId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userOptinRepository.get(userOptinId).then { UserOptin userOptin ->
                if (userOptin == null) {
                    throw AppErrors.INSTANCE.userOptinNotFound(userOptinId).exception()
                }

                if (userId != userOptin.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userOptinId doesn\'t match.').exception()
                }

                return Promise.pure(userOptin)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserOptinListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserOptin userOptin) {
        if (userId == null) {
        throw new IllegalArgumentException('userId is null')
    }
        checkBasicUserOptinInfo(userOptin)
        if (userOptin.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userOptin.userId != null && userOptin.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userOptin.userId.toString()).exception()
        }

        return userOptinRepository.search(new UserOptinListOptions(
                userId: userId,
                type: userOptin.type
        )).then { List<UserOptin> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('type').exception()
            }

            userOptin.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserOptinId userOptinId, UserOptin userOptin,
                                    UserOptin oldUserOptin) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return validateForGet(userId, userOptinId).then {
            checkBasicUserOptinInfo(userOptin)

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
                        userId: userId,
                        type: userOptin.type
                )).then { List<UserOptin> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('type').exception()
                    }
                    userOptin.setUserId(userId)
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
    }
}
