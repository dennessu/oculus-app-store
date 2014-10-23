package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeRepository
import com.junbo.identity.service.UserTFAPhoneBackupCodeService
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserTFAPhoneBackupCodeServiceImpl implements UserTFAPhoneBackupCodeService {
    private UserTFAPhoneBackupCodeRepository userTFAPhoneBackupCodeRepository

    @Override
    Promise<UserTFABackupCode> get(UserTFABackupCodeId id) {
        return userTFAPhoneBackupCodeRepository.get(id)
    }

    @Override
    Promise<UserTFABackupCode> create(UserTFABackupCode model) {
        return userTFAPhoneBackupCodeRepository.create(model)
    }

    @Override
    Promise<UserTFABackupCode> update(UserTFABackupCode model, UserTFABackupCode oldModel) {
        return userTFAPhoneBackupCodeRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeId id) {
        return userTFAPhoneBackupCodeRepository.delete(id)
    }

    @Override
    Promise<List<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userTFAPhoneBackupCodeRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return userTFAPhoneBackupCodeRepository.searchByUserIdAndActiveStatus(userId, active, limit, offset)
    }

    @Required
    void setUserTFAPhoneBackupCodeRepository(UserTFAPhoneBackupCodeRepository userTFAPhoneBackupCodeRepository) {
        this.userTFAPhoneBackupCodeRepository = userTFAPhoneBackupCodeRepository
    }
}
