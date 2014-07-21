package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFAAttemptId
import com.junbo.common.id.UserTFAId
import com.junbo.identity.data.repository.UserTFAAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFAAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFAAttemptRepositoryCloudantImpl extends CloudantClient<UserTFAAttempt>
        implements UserTFAAttemptRepository {

    @Override
    Promise<List<UserTFAAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserTFAAttempt>> searchByUserIdAndUserTFAId(UserId userId, UserTFAId userTFAId,
                                                               Integer limit, Integer offset) {
        def startKey = [userId.toString(), userTFAId.toString()]
        def endKey = [userId.toString(), userTFAId.toString()]
        return queryView('by_user_id_tfa_id_time', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<UserTFAAttempt> create(UserTFAAttempt entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFAAttempt> update(UserTFAAttempt entity, UserTFAAttempt oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserTFAAttempt> get(UserTFAAttemptId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFAAttemptId id) {
        return cloudantDelete(id.toString())
    }
}
