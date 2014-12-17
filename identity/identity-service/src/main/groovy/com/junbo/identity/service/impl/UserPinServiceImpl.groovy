package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.service.UserPinService
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserPinServiceImpl implements UserPinService {
    private UserPinRepository userPinRepository

    @Override
    Promise<UserPin> get(UserPinId id) {
        return userPinRepository.get(id)
    }

    @Override
    Promise<UserPin> create(UserPin model) {
        return userPinRepository.create(model)
    }

    @Override
    Promise<UserPin> update(UserPin model, UserPin oldModel) {
        return userPinRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
        return userPinRepository.delete(id)
    }

    @Override
    Promise<Results<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userPinRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<Results<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return userPinRepository.searchByUserIdAndActiveStatus(userId, active, limit, offset)
    }

    @Required
    void setUserPinRepository(UserPinRepository userPinRepository) {
        this.userPinRepository = userPinRepository
    }
}
