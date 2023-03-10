package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserTFABackupCodeId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserTFAPhoneBackupCodeRepository
import com.junbo.identity.spec.v1.model.UserTFABackupCode
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTFAPhoneBackupCodeRepositoryCloudantImpl extends CloudantClient<UserTFABackupCode>
        implements UserTFAPhoneBackupCodeRepository  {

    @Override
    Promise<Results<UserTFABackupCode>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryViewResults('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<UserTFABackupCode>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                                    Integer offset) {
        def startKey = [userId.toString(), active]
        def endKey = [userId.toString(), active]
        return queryViewResults('by_user_id_active', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<UserTFABackupCode> create(UserTFABackupCode entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserTFABackupCode> update(UserTFABackupCode entity, UserTFABackupCode oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserTFABackupCode> get(UserTFABackupCodeId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserTFABackupCodeId id) {
        return cloudantDelete(id.toString())
    }
}
