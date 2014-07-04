package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.data.repository.HashUserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.HashUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class HashUserPersonalInfoRepositoryImpl extends CloudantClient<HashUserPersonalInfo>
        implements HashUserPersonalInfoRepository {

    @Override
    Promise<List<HashUserPersonalInfo>> searchByHashValue(String hashSearchInfo) {
        return queryView('by_hash_value', hashSearchInfo)
    }

    @Override
    Promise<HashUserPersonalInfo> create(HashUserPersonalInfo model) {
        if (model.id == null) {
            throw new RuntimeException("HashUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPost(model)
    }

    @Override
    Promise<HashUserPersonalInfo> update(HashUserPersonalInfo model) {
        if (model.id == null) {
            throw new RuntimeException("HashUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPut(model)
    }

    @Override
    Promise<HashUserPersonalInfo> get(UserPersonalInfoId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        return cloudantDelete(id.toString())
    }
}
