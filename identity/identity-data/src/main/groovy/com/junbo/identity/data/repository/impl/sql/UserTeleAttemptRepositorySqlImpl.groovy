package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.dao.UserTeleAttemptDAO
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTFAAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleAttemptRepositorySqlImpl implements UserTFAAttemptRepository {
    private UserTeleAttemptDAO userTeleAttemptDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId,
                                                             Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<UserTFAAttempt> create(UserTFAAttempt model) {
        def entity = modelMapper.toUserTeleAttempt(model, new MappingContext())
        entity = userTeleAttemptDAO.create(entity)

        return get(new UserTFAAttemptId(entity.id))
    }

    @Override
    Promise<UserTFAAttempt> update(UserTFAAttempt model) {
        def entity = modelMapper.toUserTeleAttempt(model, new MappingContext())
        userTeleAttemptDAO.update(entity)

        return get((UserTFAAttemptId)model.id)
    }

    @Override
    Promise<UserTFAAttempt> get(UserTFAAttemptId id) {
        def entity = userTeleAttemptDAO.get(id.value)
        return Promise.pure(modelMapper.toUserTeleAttempt(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTFAAttemptId id) {
        userTeleAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }

    @Required
    void setUserTeleAttemptDAO(UserTeleAttemptDAO userTeleAttemptDAO) {
        this.userTeleAttemptDAO = userTeleAttemptDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }
}
