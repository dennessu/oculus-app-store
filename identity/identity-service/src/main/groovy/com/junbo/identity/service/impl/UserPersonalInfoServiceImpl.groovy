package com.junbo.identity.service.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.service.UserPersonalInfoService
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/22/14.
 */
@CompileStatic
class UserPersonalInfoServiceImpl implements UserPersonalInfoService {
    private UserPersonalInfoRepository userPersonalInfoRepository

    @Override
    Promise<UserPersonalInfo> get(UserPersonalInfoId id) {
        return userPersonalInfoRepository.get(id)
    }

    @Override
    Promise<UserPersonalInfo> create(UserPersonalInfo model) {
        return userPersonalInfoRepository.create(model)
    }

    @Override
    Promise<UserPersonalInfo> update(UserPersonalInfo model, UserPersonalInfo oldModel) {
        return userPersonalInfoRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        return userPersonalInfoRepository.delete(id)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByUserId(userId, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByUserIdAndType(userId, type, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByEmail(String email, Boolean isValidated, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByEmail(email, isValidated, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByPhoneNumber(String phoneNumber, Boolean isValidated, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByPhoneNumber(phoneNumber, isValidated, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByName(String name, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByName(name, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByUserIdAndValidateStatus(UserId userId, String type, Boolean isValidated, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByUserIdAndValidateStatus(userId, type, isValidated, limit, offset)
    }

    @Override
    Promise<List<UserPersonalInfo>> searchByCanonicalUsername(String canonicalUsername, Integer limit, Integer offset) {
        return userPersonalInfoRepository.searchByCanonicalUsername(canonicalUsername, limit, offset)
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }
}
