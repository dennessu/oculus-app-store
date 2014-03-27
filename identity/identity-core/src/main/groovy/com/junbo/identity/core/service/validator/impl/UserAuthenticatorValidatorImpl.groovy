package com.junbo.identity.core.service.validator.impl
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserAuthenticatorValidator
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
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
    Promise<Void> validateForGet(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }
        if (userAuthenticatorId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userAuthenticatorId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userAuthenticatorRepository.get(userAuthenticatorId).then { UserAuthenticator userAuthenticator ->
                if (userAuthenticator == null) {
                    throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
                }

                if (userId != userAuthenticator.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userAuthenticatorId doesn\'t match').
                            exception()
                }

                return Promise.pure(userAuthenticator)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserAuthenticatorListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.value == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or value').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserAuthenticator userAuthenticator) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        basicCheckUserAuthenticator(userAuthenticator)
        if (userAuthenticator.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userAuthenticator.userId != null && userAuthenticator.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userAuthenticator.userId.toString()).exception()
        }

        return userAuthenticatorRepository.search(new UserAuthenticatorListOptions(
                userId: userAuthenticator.userId,
                type: userAuthenticator.type,
                value: userAuthenticator.value
        )).then { List<UserAuthenticator> existing ->
            if (existing != null && existing.size() != 0) {
                throw AppErrors.INSTANCE.fieldDuplicate('value').exception()
            }

            userAuthenticator.setUserId(userId)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                    UserAuthenticator authenticator, UserAuthenticator oldAuthenticator) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        validateForGet(userId, userAuthenticatorId).then {
            basicCheckUserAuthenticator(authenticator)

            if (authenticator.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (authenticator.id != userAuthenticatorId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userAuthenticatorId.value.toString()).exception()
            }

            if (authenticator.id != oldAuthenticator.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldAuthenticator.id.toString()).exception()
            }

            // todo:    Liangfu:    Here we have the assumption that value should always different
            // todo:    each different type should have different value
            if (authenticator.value != oldAuthenticator.value) {
                userAuthenticatorRepository.search(new UserAuthenticatorListOptions(value: authenticator.value)).then {
                    List<UserAuthenticator> existing ->
                        if (existing != null && existing.size() != 0) {
                            throw AppErrors.INSTANCE.fieldDuplicate('value').exception()
                        }
                }
                authenticator.setUserId(userId)
                return Promise.pure(null)
            }
        }
    }

    private void basicCheckUserAuthenticator(UserAuthenticator userAuthenticator) {
        if (userAuthenticator == null) {
            throw new IllegalArgumentException('userAuthenticator is null')
        }

        if (userAuthenticator.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (userAuthenticator.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }

        if (!(userAuthenticator.type in allowedTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
        }
    }
}
