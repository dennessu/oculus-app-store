package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.service.UserPasswordService
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class UserPasswordServiceImpl implements UserPasswordService {
    private UserPasswordRepository userPasswordRepository

    @Override
    Promise<UserPassword> get(UserPasswordId id) {
        return userPasswordRepository.get(id)
    }

    @Override
    Promise<UserPassword> create(UserPassword model) {
        return userPasswordRepository.create(model)
    }

    @Override
    Promise<UserPassword> update(UserPassword model, UserPassword oldModel) {
        return userPasswordRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserPasswordId id) {
        return userPasswordRepository.delete(id)
    }

    @Override
    Promise<List<UserPassword>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userPasswordRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserPassword>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return userPasswordRepository.searchByUserIdAndActiveStatus(userId, active, limit, offset)
    }

    @Required
    void setUserPasswordRepository(UserPasswordRepository userPasswordRepository) {
        this.userPasswordRepository = userPasswordRepository
    }
}
