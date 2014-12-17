package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFAPhoneBackupCodeAttemptRepositoryCloudantImpl extends CloudantClient<UserTFABackupCodeAttempt>
        implements UserTFAPhoneBackupCodeAttemptRepository  {

    @Override
    Promise<Results<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        def startKey = [userId.toString()]
        def endKey = [userId.toString()]
        return queryViewResults('by_user_id', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt entity, UserTFABackupCodeAttempt oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> get(UserTFABackupCodeAttemptId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeAttemptId id) {
        return cloudantDelete(id.toString())
    }
}
