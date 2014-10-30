package com.junbo.identity.service.impl

import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.service.UserAuthenticatorService
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class UserAuthenticatorServiceImpl implements UserAuthenticatorService {
    private UserAuthenticatorRepository userAuthenticatorRepository

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId id) {
        return userAuthenticatorRepository.get(id)
    }

    @Override
    Promise<UserAuthenticator> create(UserAuthenticator model) {
        return userAuthenticatorRepository.create(model)
    }

    @Override
    Promise<UserAuthenticator> update(UserAuthenticator model, UserAuthenticator oldModel) {
        return userAuthenticatorRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserAuthenticatorId id) {
        return userAuthenticatorRepository.delete(id)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByUserIdAndType(userId, type, limit, offset)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByExternalId(String externalId, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByExternalId(externalId, limit, offset)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndTypeAndExternalId(UserId userId, String type, String externalId, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByUserIdAndTypeAndExternalId(userId, type, externalId, limit, offset)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndExternalId(UserId userId, String externalId, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByUserIdAndExternalId(userId, externalId, limit, offset)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByExternalIdAndType(String externalId, String type, Integer limit, Integer offset) {
        return userAuthenticatorRepository.searchByExternalIdAndType(externalId, type, limit, offset)
    }

    @Required
    void setUserAuthenticatorRepository(UserAuthenticatorRepository userAuthenticatorRepository) {
        this.userAuthenticatorRepository = userAuthenticatorRepository
    }
}
