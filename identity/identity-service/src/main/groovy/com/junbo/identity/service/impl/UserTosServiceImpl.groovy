package com.junbo.identity.service.impl

import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.service.UserTosService
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTosServiceImpl implements UserTosService {
    private UserTosRepository userTosRepository

    @Override
    Promise<UserTosAgreement> get(UserTosAgreementId id) {
        return userTosRepository.get(id)
    }

    @Override
    Promise<UserTosAgreement> create(UserTosAgreement model) {
        return userTosRepository.create(model)
    }

    @Override
    Promise<UserTosAgreement> update(UserTosAgreement model, UserTosAgreement oldModel) {
        return userTosRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTosAgreementId id) {
        return userTosRepository.delete(id)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userTosRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByTosId(TosId tosId, Integer limit, Integer offset) {
        return userTosRepository.searchByTosId(tosId, limit, offset)
    }

    @Override
    Promise<List<UserTosAgreement>> searchByUserIdAndTosId(UserId userId, TosId tosId, Integer limit, Integer offset) {
        return userTosRepository.searchByUserIdAndTosId(userId, tosId, limit, offset)
    }

    @Required
    void setUserTosRepository(UserTosRepository userTosRepository) {
        this.userTosRepository = userTosRepository
    }
}
