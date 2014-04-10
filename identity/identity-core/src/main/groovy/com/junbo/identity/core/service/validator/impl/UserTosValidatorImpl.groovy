package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosId
import com.junbo.identity.core.service.validator.UserTosValidator
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.identity.spec.options.list.UserTosListOptions
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserTosValidatorImpl implements UserTosValidator {

    private UserRepository userRepository

    private UserTosRepository userTosRepository

    private Integer tosUriMinLength
    private Integer tosUriMaxLength

    @Override
    Promise<UserTos> validateForGet(UserId userId, UserTosId userTosId) {

        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userTosId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userTosId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return userTosRepository.get(userTosId).then { UserTos userTos ->
                if (userTos == null) {
                    throw AppErrors.INSTANCE.userTosNotFound(userTosId).exception()
                }

                if (userId != userTos.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userTosId doesn\'t match.').exception()
                }

                return Promise.pure(userTos)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTosListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTos userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserTosInfo(userTos)
        if (userTos.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userTos.userId != null && userTos.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userTos.userId.toString()).exception()
        }

        return userTosRepository.search(new UserTosListOptions(
                userId: userId,
                tosUri: userTos.tosUri
        )).then { List<UserTos> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('tosUri').exception()
            }

            userTos.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTosId userTosId, UserTos userTos, UserTos oldUserTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return validateForGet(userId, userTosId).then {
            checkBasicUserTosInfo(userTos)

            if (userTos.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userTos.id != userTosId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userTosId.value.toString()).exception()
            }

            if (userTos.id != oldUserTos.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserTos.id.toString()).exception()
            }

            if (userTos.tosUri != oldUserTos.tosUri) {
                return userTosRepository.search(new UserTosListOptions(
                        userId: userId,
                        tosUri: userTos.tosUri
                )).then { List<UserTos> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('tosUri').exception()
                    }
                    userTos.setUserId(userId)
                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    private void checkBasicUserTosInfo(UserTos userTos) {
        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        if (userTos.tosUri == null) {
            throw AppErrors.INSTANCE.fieldRequired('tosUri').exception()
        }

        if (userTos.tosUri.size() < tosUriMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('tosUri', tosUriMinLength).exception()
        }

        if (userTos.tosUri.size() > tosUriMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('tosUri', tosUriMaxLength).exception()
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserTosRepository(UserTosRepository userTosRepository) {
        this.userTosRepository = userTosRepository
    }

    @Required
    void setTosUriMinLength(Integer tosUriMinLength) {
        this.tosUriMinLength = tosUriMinLength
    }

    @Required
    void setTosUriMaxLength(Integer tosUriMaxLength) {
        this.tosUriMaxLength = tosUriMaxLength
    }
}
