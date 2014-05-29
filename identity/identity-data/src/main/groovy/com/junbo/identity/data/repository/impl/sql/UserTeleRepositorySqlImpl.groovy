package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTeleId
import com.junbo.identity.data.dao.UserTeleDAO
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleRepositorySqlImpl implements UserTeleRepository {
    private UserTeleDAO userTeleDAO
    private ModelMapper modelMapper

    @Override
    Promise<List<UserTeleCode>> searchTeleCodeByUserIdAndPhone(UserId userId, UserPersonalInfoId phoneNumber,
                                                               Integer limit, Integer offset) {
        // def entity = userTeleDAO.getActiveUserTeleCode(userId, phoneNumber)

        // return get(new UserTeleId(entity.id))
        return Promise.pure(null)
    }

    @Override
    Promise<UserTeleCode> create(UserTeleCode model) {
        def entity = modelMapper.toUserTeleCode(model, new MappingContext())
        entity = userTeleDAO.create(entity)

        return get(new UserTeleId(entity.id))
    }

    @Override
    Promise<UserTeleCode> update(UserTeleCode model) {
        def entity = modelMapper.toUserTeleCode(model, new MappingContext())
        userTeleDAO.update(entity)

        return get((UserTeleId)model.id)
    }

    @Override
    Promise<UserTeleCode> get(UserTeleId id) {
        def entity = userTeleDAO.get(id.value)
        return Promise.pure(modelMapper.toUserTeleCode(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(UserTeleId id) {
        userTeleDAO.delete(id.value)
        return Promise.pure(null)
    }

    @Required
    void setUserTeleDAO(UserTeleDAO userTeleDAO) {
        this.userTeleDAO = userTeleDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }
}
