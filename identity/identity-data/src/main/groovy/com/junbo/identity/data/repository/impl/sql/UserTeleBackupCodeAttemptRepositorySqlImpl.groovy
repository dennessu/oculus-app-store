package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTeleBackupCodeAttemptId
import com.junbo.identity.data.dao.UserTeleBackupCodeAttemptDAO
import com.junbo.identity.data.entity.user.UserTeleBackupCodeAttemptEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTeleBackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTeleBackupCodeAttempt
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeAttemptRepositorySqlImpl implements UserTeleBackupCodeAttemptRepository {
    private UserTeleBackupCodeAttemptDAO userTeleBackupCodeAttemptDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTeleBackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> create(UserTeleBackupCodeAttempt model) {
        def entity = modelMapper.toUserTeleBackupCodeAttempt(model, new MappingContext())

        entity = userTeleBackupCodeAttemptDAO.create(entity)

        return get(new UserTeleBackupCodeAttemptId(entity.id))
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> update(UserTeleBackupCodeAttempt model) {
        def entity = modelMapper.toUserTeleBackupCodeAttempt(model, new MappingContext())

        userTeleBackupCodeAttemptDAO.update(entity)

        return get((UserTeleBackupCodeAttemptId)model.id)
    }

    @Override
    Promise<UserTeleBackupCodeAttempt> get(UserTeleBackupCodeAttemptId id) {
        def entity = userTeleBackupCodeAttemptDAO.get(id.value)

        return Promise.pure(modelMapper.toUserTeleBackupCodeAttempt(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTeleBackupCodeAttemptId id) {
        userTeleBackupCodeAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }

    @Required
    void setUserTeleBackupCodeAttemptDAO(UserTeleBackupCodeAttemptDAO userTeleBackupCodeAttemptDAO) {
        this.userTeleBackupCodeAttemptDAO = userTeleBackupCodeAttemptDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }
}
