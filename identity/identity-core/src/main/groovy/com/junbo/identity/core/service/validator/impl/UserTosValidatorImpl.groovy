package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.core.service.validator.UserTosValidator
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
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

    private TosRepository tosRepository

    @Override
    Promise<UserTosAgreement> validateForGet(UserId userId, UserTosAgreementId userTosId) {

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

            return userTosRepository.get(userTosId).then { UserTosAgreement userTos ->
                if (userTos == null) {
                    throw AppErrors.INSTANCE.userTosAgreementNotFound(userTosId).exception()
                }

                if (userId != userTos.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userTosAgreementId doesn\'t match.').
                            exception()
                }

                return Promise.pure(userTos)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTosAgreementListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserTosAgreement userTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userTos.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userTos.userId != null && userTos.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userTos.userId.toString()).exception()
        }
        return checkBasicUserTosInfo(userTos).then {
            return userTosRepository.search(new UserTosAgreementListOptions(
                    userId: userId,
                    tosId: userTos.tosId
            )).then { List<UserTosAgreement> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppErrors.INSTANCE.fieldDuplicate('tosId').exception()
                }

                userTos.setUserId(userId)
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserTosAgreementId userTosId,
                                    UserTosAgreement userTos, UserTosAgreement oldUserTos) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return validateForGet(userId, userTosId).then {
            return checkBasicUserTosInfo(userTos)
        }.then {
            if (userTos.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userTos.id != userTosId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userTosId.value.toString()).exception()
            }

            if (userTos.id != oldUserTos.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserTos.id.toString()).exception()
            }

            if (userTos.tosId != oldUserTos.tosId) {
                return userTosRepository.search(new UserTosAgreementListOptions(
                        userId: userId,
                        tosId: userTos.tosId
                )).then { List<UserTosAgreement> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('tosId').exception()
                    }
                    userTos.setUserId(userId)
                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicUserTosInfo(UserTosAgreement userTos) {
        if (userTos == null) {
            throw new IllegalArgumentException('userTos is null')
        }

        if (userTos.tosId == null) {
            throw AppErrors.INSTANCE.fieldRequired('tosId').exception()
        }

        return tosRepository.get(userTos.tosId).then { Tos tos ->
            if (tos == null) {
                throw AppErrors.INSTANCE.tosNotFound(userTos.tosId).exception()
            }

            return Promise.pure(null)
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
    void setTosRepository(TosRepository tosRepository) {
        this.tosRepository = tosRepository
    }
}
