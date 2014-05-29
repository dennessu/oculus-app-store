package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleAttemptId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.data.dao.UserTeleAttemptDAO
import com.junbo.identity.data.entity.user.UserTeleAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTeleAttemptRepository
import com.junbo.identity.spec.v1.model.UserTeleAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleAttemptRepositorySqlImpl implements UserTeleAttemptRepository {
    private UserTeleAttemptDAO userTeleAttemptDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTeleAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserTeleAttempt>> searchByUserIdAndUserTeleId(UserId userId, UserTeleId userTeleId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<UserTeleAttempt> create(UserTeleAttempt model) {
        def entity = modelMapper.toUserTeleAttempt(model, new MappingContext())
        entity = userTeleAttemptDAO.create(entity)

        return get(new UserTeleAttemptId(entity.id))
    }

    @Override
    Promise<UserTeleAttempt> update(UserTeleAttempt model) {
        def entity = modelMapper.toUserTeleAttempt(model, new MappingContext())
        userTeleAttemptDAO.update(entity)

        return get((UserTeleAttemptId)model.id)
    }

    @Override
    Promise<UserTeleAttempt> get(UserTeleAttemptId id) {
        def entity = userTeleAttemptDAO.get(id.value)
        return Promise.pure(modelMapper.toUserTeleAttempt(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTeleAttemptId id) {
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
