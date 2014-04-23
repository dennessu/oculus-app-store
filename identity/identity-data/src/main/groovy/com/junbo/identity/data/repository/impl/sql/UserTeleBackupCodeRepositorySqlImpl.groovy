package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.data.dao.UserTeleBackupCodeDAO
import com.junbo.identity.data.entity.user.UserTeleBackupCodeEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTeleBackupCodeRepository
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeRepositorySqlImpl implements UserTeleBackupCodeRepository {
    private UserTeleBackupCodeDAO userTeleBackupCodeDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTeleBackupCode>> search(UserTeleBackupCodeListOptions listOptions) {
        def result = []
        def list = userTeleBackupCodeDAO.search(listOptions)

        list.each { UserTeleBackupCodeEntity entity ->
            result.add(modelMapper.toUserTeleBackupCode(entity, new MappingContext()))
        }
        return Promise.pure(result)
    }

    @Override
    Promise<UserTeleBackupCode> create(UserTeleBackupCode model) {
        def entity = modelMapper.toUserTeleBackupCode(model, new MappingContext())
        entity = userTeleBackupCodeDAO.create(entity)

        return get(new UserTeleBackupCodeId(entity.id))
    }

    @Override
    Promise<UserTeleBackupCode> update(UserTeleBackupCode model) {
        def entity = modelMapper.toUserTeleBackupCode(model, new MappingContext())
        userTeleBackupCodeDAO.update(entity)

        return get(new UserTeleBackupCodeId(entity.id))
    }

    @Override
    Promise<UserTeleBackupCode> get(UserTeleBackupCodeId id) {
        def entity = userTeleBackupCodeDAO.get(id.value)

        return Promise.pure(modelMapper.toUserTeleBackupCode(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTeleBackupCodeId id) {
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
