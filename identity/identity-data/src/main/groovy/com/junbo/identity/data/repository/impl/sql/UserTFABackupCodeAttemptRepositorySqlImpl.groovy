package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.data.dao.UserTeleBackupCodeAttemptDAO
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTFABackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFABackupCodeAttemptRepositorySqlImpl implements UserTFABackupCodeAttemptRepository {
    private UserTeleBackupCodeAttemptDAO userTFABackupCodeAttemptDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt model) {
        def entity = modelMapper.toUserTeleBackupCodeAttempt(model, new MappingContext())

        entity = userTFABackupCodeAttemptDAO.create(entity)

        return get(new UserTFABackupCodeAttemptId(entity.id))
    }

    @Override
    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt model) {
        def entity = modelMapper.toUserTeleBackupCodeAttempt(model, new MappingContext())

        userTFABackupCodeAttemptDAO.update(entity)

        return get((UserTFABackupCodeAttemptId)model.id)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> get(UserTFABackupCodeAttemptId id) {
        def entity = userTFABackupCodeAttemptDAO.get(id.value)

        return Promise.pure(modelMapper.toUserTeleBackupCodeAttempt(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeAttemptId id) {
        userTFABackupCodeAttemptDAO.delete(id.value)
        return Promise.pure(null)
    }

    @Required
    void setUserTeleBackupCodeAttemptDAO(UserTeleBackupCodeAttemptDAO userTeleBackupCodeAttemptDAO) {
        this.userTFABackupCodeAttemptDAO = userTeleBackupCodeAttemptDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }
}
