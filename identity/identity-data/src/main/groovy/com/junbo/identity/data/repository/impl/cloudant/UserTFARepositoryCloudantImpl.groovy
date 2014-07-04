package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.repository.UserTFARepository
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFARepositoryCloudantImpl extends CloudantClient<UserTFA> implements UserTFARepository  {

    @Override
    Promise<List<UserTFA>> searchTFACodeByUserIdAndPersonalInfoId(UserId userId, UserPersonalInfoId personalInfo,
                                                               Integer limit, Integer offset) {
        def startKey = [userId.toString(), personalInfo.toString()]
        def endKey = [userId.toString(), personalInfo.toString()]
        return queryView('by_user_id_personal_info', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<UserTFA> create(UserTFA entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFA> update(UserTFA entity) {
        return cloudantPut(entity)
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
