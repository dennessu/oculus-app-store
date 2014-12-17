package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserTFAPhoneRepository
import com.junbo.identity.service.UserTFAPhoneService
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTFAPhoneServiceImpl implements UserTFAPhoneService {
    private UserTFAPhoneRepository userTFAPhoneRepository

    @Override
    Promise<UserTFA> get(UserTFAId id) {
        return userTFAPhoneRepository.get(id)
    }

    @Override
    Promise<UserTFA> create(UserTFA model) {
        return userTFAPhoneRepository.create(model)
    }

    @Override
    Promise<UserTFA> update(UserTFA model, UserTFA oldModel) {
        return userTFAPhoneRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTFAId id) {
        return userTFAPhoneRepository.delete(id)
    }

    @Override
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfoId, Integer limit, Integer offset) {
        return userTFAPhoneRepository.searchTFACodeByUserIdAndPersonalInfoId(userId, personalInfoId, limit, offset)
    }

    @Override
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPIIAfterTime(UserId userId, UserPersonalInfoId personalInfoId, Integer limit, Integer offset, Long startTimeOffset) {
        return userTFAPhoneRepository.searchTFACodeByUserIdAndPIIAfterTime(userId, personalInfoId, limit, offset, startTimeOffset)
    }

    @Required
    void setUserTFAPhoneRepository(UserTFAPhoneRepository userTFAPhoneRepository) {
        this.userTFAPhoneRepository = userTFAPhoneRepository
    }
}
