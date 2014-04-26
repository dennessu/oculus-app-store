package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.core.service.validator.UserAuthenticatorValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
class UserAuthenticatorValidatorImpl implements UserAuthenticatorValidator {

    private UserRepository userRepository

    private UserAuthenticatorRepository userAuthenticatorRepository

    private List<String> allowedTypes

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserAuthenticatorRepository(UserAuthenticatorRepository userAuthenticatorRepository) {
        this.userAuthenticatorRepository = userAuthenticatorRepository
    }

    @Required
    void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes
    }

    @Override
    Promise<UserAuthenticator> validateForGet(UserAuthenticatorId userAuthenticatorId) {
        if (userAuthenticatorId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userAuthenticatorId').exception()
        }

        return userAuthenticatorRepository.get(userAuthenticatorId).then { UserAuthenticator userAuthenticator ->
                if (userAuthenticator == null) {
                    throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
                }

                if (userAuthenticator.userId == null) {
                    throw new IllegalAccessException('userId is not found')
                }

                return userRepository.get(userAuthenticator.userId).then { User user ->
                    if (user == null) {
                        throw AppErrors.INSTANCE.userNotFound(userAuthenticator.userId).exception()
                    }

                    if (user.isAnonymous == false) {
                        throw AppErrors.INSTANCE.userInInvalidStatus(userAuthenticator.userId).exception()
                    }

                    if (user.status != UserStatus.ACTIVE.toString()) {
                        throw AppErrors.INSTANCE.userInInvalidStatus(userAuthenticator.userId).exception()
                    }

                    return Promise.pure(userAuthenticator)
                }
            }
    }

    @Override
    Promise<Void> validateForSearch(AuthenticatorListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.externalId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or externalId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserAuthenticator userAuthenticator) {
        return basicCheckUserAuthenticator(userAuthenticator).then {
            if (userAuthenticator.id != null) {
                throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
            }

            return userAuthenticatorRepository.search(new AuthenticatorListOptions(
                    userId: userAuthenticator.userId,
                    type: userAuthenticator.type,
                    externalId: userAuthenticator.externalId
            )).then { List<UserAuthenticator> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppErrors.INSTANCE.fieldDuplicate('externalId').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserAuthenticatorId userAuthenticatorId, UserAuthenticator authenticator,
                                    UserAuthenticator oldAuthenticator) {
        return validateForGet(userAuthenticatorId).then {
            return basicCheckUserAuthenticator(authenticator)
        }.then {
            if (authenticator.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (authenticator.id != userAuthenticatorId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userAuthenticatorId.value.toString()).exception()
            }

            if (authenticator.id != oldAuthenticator.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldAuthenticator.id.toString()).exception()
            }

            if (authenticator.userId != oldAuthenticator.userId) {
                throw AppErrors.INSTANCE.fieldInvalid('userId', oldAuthenticator.userId.toString()).exception()
            }

            if (authenticator.externalId != oldAuthenticator.externalId
             || authenticator.type != oldAuthenticator.type) {
                userAuthenticatorRepository.search(new AuthenticatorListOptions(
                        userId: authenticator.userId,
                        externalId: authenticator.externalId,
                        type: authenticator.type
                )).then {
                    List<UserAuthenticator> existing ->
                        if (!CollectionUtils.isEmpty(existing)) {
                            throw AppErrors.INSTANCE.fieldDuplicate('type or externalId').exception()
                        }
                }
                return Promise.pure(null)
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> basicCheckUserAuthenticator(UserAuthenticator userAuthenticator) {
        if (userAuthenticator == null) {
            throw new IllegalArgumentException('userAuthenticator is null')
        }

        if (userAuthenticator.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userRepository.get(userAuthenticator.userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userAuthenticator.userId).exception()
            }

            if (user.isAnonymous == null) {
                throw AppErrors.INSTANCE.fieldRequired('isAnonymous').exception()
            }

            if (user.isAnonymous == false) {
                throw AppErrors.INSTANCE.fieldInvalid('isAnonymous', 'true').exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userAuthenticator.userId).exception()
            }

            if (userAuthenticator.externalId == null) {
                throw AppErrors.INSTANCE.fieldRequired('externalId').exception()
            }

            if (userAuthenticator.type == null) {
                throw AppErrors.INSTANCE.fieldRequired('type').exception()
            }

            if (!(userAuthenticator.type in allowedTypes)) {
                throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
            }

            return Promise.pure(null)
        }
    }
}
