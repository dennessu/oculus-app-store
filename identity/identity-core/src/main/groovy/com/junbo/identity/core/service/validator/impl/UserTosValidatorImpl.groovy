package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.core.service.validator.UserTosValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.service.TosService
import com.junbo.identity.service.UserService
import com.junbo.identity.service.UserTosService
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

    private UserService userService
    private UserTosService userTosService
    private TosService tosService

    @Override
    Promise<UserTosAgreement> validateForGet(UserTosAgreementId userTosId) {

        if (userTosId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userTosId').exception()
        }

        return userTosService.get(userTosId).then { UserTosAgreement userTos ->
            if (userTos == null) {
                throw AppErrors.INSTANCE.userTosAgreementNotFound(userTosId).exception()
            }

            return Promise.pure(userTos)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserTosAgreementListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.tosId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId or tosId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserTosAgreement userTos) {
        if (userTos.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }
        return checkBasicUserTosInfo(userTos).then {
            return userTosService.searchByUserIdAndTosId(userTos.userId, userTos.tosId, 1, 0).then {
                List<UserTosAgreement> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('tosId').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserTosAgreementId userTosId, UserTosAgreement userTos, UserTosAgreement oldUserTos) {
        return validateForGet(userTosId).then {
            return checkBasicUserTosInfo(userTos)
        }.then {
            if (userTos.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userTos.id != userTosId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userTos.id, userTosId).exception()
            }

            if (userTos.id != oldUserTos.id) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userTos.id, oldUserTos.id).exception()
            }

            if (userTos.tosId != oldUserTos.tosId) {
                return userTosService.searchByUserIdAndTosId(userTos.userId, userTos.tosId, 1, 0).then {
                    List<UserTosAgreement> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppCommonErrors.INSTANCE.fieldDuplicate('tosId').exception()
                    }

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

        if (userTos.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        if (userTos.tosId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('tosId').exception()
        }

        if (userTos.agreementTime == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('agreementTime').exception()
        }

        if (userTos.agreementTime.after(new Date())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('agreementTime').exception()
        }

        return tosService.get(userTos.tosId).then { Tos tos ->
            if (tos == null) {
                throw AppErrors.INSTANCE.tosNotFound(userTos.tosId).exception()
            }

            return userService.getNonDeletedUser(userTos.userId).then { User user ->
                if (user == null) {
                    throw AppErrors.INSTANCE.userNotFound(userTos.userId).exception()
                }

                if (user.isAnonymous || user.status != UserStatus.ACTIVE.toString()) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userTos.userId).exception()
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
    void setUserTosService(UserTosService userTosService) {
        this.userTosService = userTosService
    }

    @Required
    void setTosService(TosService tosService) {
        this.tosService = tosService
    }
}
