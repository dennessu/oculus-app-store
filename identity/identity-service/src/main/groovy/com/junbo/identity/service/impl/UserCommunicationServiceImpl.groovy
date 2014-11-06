package com.junbo.identity.service.impl

import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.service.UserCommunicationService
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class UserCommunicationServiceImpl implements UserCommunicationService {
    private UserCommunicationRepository userCommunicationRepository

    @Override
    Promise<UserCommunication> get(UserCommunicationId id) {
        return userCommunicationRepository.get(id)
    }

    @Override
    Promise<UserCommunication> create(UserCommunication model) {
        return userCommunicationRepository.create(model)
    }

    @Override
    Promise<UserCommunication> update(UserCommunication model, UserCommunication oldModel) {
        return userCommunicationRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserCommunicationId id) {
        return userCommunicationRepository.delete(id)
    }

    @Override
    Promise<Results<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userCommunicationRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<Results<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit, Integer offset) {
        return userCommunicationRepository.searchByCommunicationId(communicationId, limit, offset)
    }

    @Override
    Promise<Results<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId, Integer limit, Integer offset) {
        return userCommunicationRepository.searchByUserIdAndCommunicationId(userId, communicationId, limit, offset)
    }

    @Required
    void setUserCommunicationRepository(UserCommunicationRepository userCommunicationRepository) {
        this.userCommunicationRepository = userCommunicationRepository
    }
}
