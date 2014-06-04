package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.data.dao.UserTeleBackupCodeDAO
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTFABackupCodeRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeRepositorySqlImpl implements UserTFABackupCodeRepository {
    private UserTeleBackupCodeDAO userTeleBackupCodeDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return Promise.pure(null)
    }

    @Override
    Promise<UserTFABackupCode> create(UserTFABackupCode model) {
        def entity = modelMapper.toUserTeleBackupCode(model, new MappingContext())
        entity = userTeleBackupCodeDAO.create(entity)

        return get(new UserTFABackupCodeId(entity.id))
    }

    @Override
    Promise<UserTFABackupCode> update(UserTFABackupCode model) {
        def entity = modelMapper.toUserTeleBackupCode(model, new MappingContext())
        userTeleBackupCodeDAO.update(entity)

        return get(new UserTFABackupCodeId(entity.id))
    }

    @Override
    Promise<UserTFABackupCode> get(UserTFABackupCodeId id) {
        def entity = userTeleBackupCodeDAO.get(id.value)

        return Promise.pure(modelMapper.toUserTeleBackupCode(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeId id) {
        userTeleBackupCodeDAO.delete(id.value)
        return Promise.pure(null)
    }

    @Required
    void setUserTeleBackupCodeDAO(UserTeleBackupCodeDAO userTeleBackupCodeDAO) {
        this.userTeleBackupCodeDAO = userTeleBackupCodeDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }
}
