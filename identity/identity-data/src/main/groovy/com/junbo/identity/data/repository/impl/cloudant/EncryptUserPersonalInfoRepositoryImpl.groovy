package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.EncryptUserPersonalInfoId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.data.repository.EncryptUserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils
/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class EncryptUserPersonalInfoRepositoryImpl extends CloudantClient<EncryptUserPersonalInfo>
        implements EncryptUserPersonalInfoRepository {

    @Override
    Promise<EncryptUserPersonalInfo> searchByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId) {
        return queryView('by_user_personal_info_id', userPersonalInfoId.value.toString()).then { List<EncryptUserPersonalInfo> list ->

            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure((EncryptUserPersonalInfo)list.get(0))
        }
    }

    @Override
    Promise<List<EncryptUserPersonalInfo>> searchByHashValue(String hashSearchInfo) {
        return queryView('by_hash_value', hashSearchInfo)
    }

    @Override
    Promise<EncryptUserPersonalInfo> create(EncryptUserPersonalInfo model) {
        if (model.id == null) {
            model.id = new EncryptUserPersonalInfoId(model.userPersonalInfoId.value)
        }
        return cloudantPost(model)
    }

    @Override
    Promise<EncryptUserPersonalInfo> update(EncryptUserPersonalInfo model) {
        if (model.id == null) {
            model.id = new EncryptUserPersonalInfoId(model.userPersonalInfoId.value)
        }
        return cloudantPut(model)
    }

    @Override
    Promise<EncryptUserPersonalInfo> get(EncryptUserPersonalInfoId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(EncryptUserPersonalInfoId id) {
        return cloudantDelete(id.toString())
    }
}
