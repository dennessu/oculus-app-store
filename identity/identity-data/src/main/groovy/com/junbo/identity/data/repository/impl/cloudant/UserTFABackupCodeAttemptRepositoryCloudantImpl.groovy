package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeAttemptId
import com.junbo.identity.data.repository.UserTFABackupCodeAttemptRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCodeAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFABackupCodeAttemptRepositoryCloudantImpl extends CloudantClient<UserTFABackupCodeAttempt>
        implements UserTFABackupCodeAttemptRepository  {

    @Override
    Promise<List<UserTFABackupCodeAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> create(UserTFABackupCodeAttempt entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFABackupCodeAttempt> update(UserTFABackupCodeAttempt entity) {
        return cloudantPut(entity)
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
