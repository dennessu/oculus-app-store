package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.repository.UserTFAMailRepository
import com.junbo.identity.service.UserTFAMailService
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTFAMailServiceImpl implements UserTFAMailService {
    private UserTFAMailRepository userTFAMailRepository

    @Override
    Promise<UserTFA> get(UserTFAId id) {
        return userTFAMailRepository.get(id)
    }

    @Override
    Promise<UserTFA> create(UserTFA model) {
        return userTFAMailRepository.create(model)
    }

    @Override
    Promise<UserTFA> update(UserTFA model, UserTFA oldModel) {
        return userTFAMailRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTFAId id) {
        return userTFAMailRepository.delete(id)
    }

    @Override
    Promise<List<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfoId, Integer limit, Integer offset) {
        return userTFAMailRepository.searchTFACodeByUserIdAndPersonalInfoId(userId, personalInfoId, limit, offset)
    }

    @Override
    Promise<List<UserTFA>> searchTFACodeByUserIdAndPIIAfterTime(UserId userId, UserPersonalInfoId personalInfoId, Integer limit, Integer offset, Long startTimeOffset) {
        return userTFAMailRepository.searchTFACodeByUserIdAndPIIAfterTime(userId, personalInfoId, limit, offset, startTimeOffset)
    }

    @Required
    void setUserTFAMailRepository(UserTFAMailRepository userTFAMailRepository) {
        this.userTFAMailRepository = userTFAMailRepository
    }
}
