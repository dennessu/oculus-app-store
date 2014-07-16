package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClientRouted
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.data.repository.EncryptUserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class EncryptUserPersonalInfoRepositoryImpl extends CloudantClientRouted<EncryptUserPersonalInfo>
        implements EncryptUserPersonalInfoRepository {

    @Override
    Promise<EncryptUserPersonalInfo> create(EncryptUserPersonalInfo model) {
        if (model.id == null) {
            throw new RuntimeException("EncryptUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPost(model)
    }

    @Override
    Promise<EncryptUserPersonalInfo> update(EncryptUserPersonalInfo model, EncryptUserPersonalInfo oldModel) {
        if (model.id == null) {
            throw new RuntimeException("EncryptUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<EncryptUserPersonalInfo> get(UserPersonalInfoId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        return cloudantDelete(id.toString())
    }
}
