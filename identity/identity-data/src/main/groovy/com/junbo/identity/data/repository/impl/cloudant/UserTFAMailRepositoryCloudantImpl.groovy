package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserTFAMailRepository
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 7/31/2014.
 */
@CompileStatic
class UserTFAMailRepositoryCloudantImpl extends CloudantClient<UserTFA> implements UserTFAMailRepository {

    @Override
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfo,
                                                                  Integer limit, Integer offset) {
        def startKey = [userId.toString(), personalInfo.toString()]
        def endKey = [userId.toString(), personalInfo.toString()]
        return queryViewResults('by_user_id_personal_info', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<Results<UserTFA>> searchTFACodeByUserIdAndPIIAfterTime(UserId userId, UserPersonalInfoId personalInfoId, Integer limit, Integer offset, Long startTimeOffset) {
        def startKey = [userId.toString(), personalInfoId.toString(), startTimeOffset]
        def endKey = [userId.toString(), personalInfoId.toString()]
        return queryViewResults('by_user_id_personal_info', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<UserTFA> create(UserTFA entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFA> update(UserTFA entity, UserTFA oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserTFA> get(UserTFAId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFAId id) {
        return cloudantDelete(id.toString())
    }
}
