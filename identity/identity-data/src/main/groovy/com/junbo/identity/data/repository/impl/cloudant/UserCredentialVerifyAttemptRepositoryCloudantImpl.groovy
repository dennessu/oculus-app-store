package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserCredentialVerifyAttemptId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserCredentialVerifyAttemptRepository
import com.junbo.identity.spec.v1.model.UserCredentialVerifyAttempt
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserCredentialVerifyAttemptRepositoryCloudantImpl extends CloudantClient<UserCredentialVerifyAttempt>
        implements UserCredentialVerifyAttemptRepository{

    @Override
    Promise<UserCredentialVerifyAttempt> create(UserCredentialVerifyAttempt entity) {
        entity.setValue(null)
        return cloudantPost(entity)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> update(UserCredentialVerifyAttempt entity) {
        return cloudantPut(entity)
    }

    @Override
    Promise<UserCredentialVerifyAttempt> get(UserCredentialVerifyAttemptId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByUserIdAndCredentialTypeAndInterval(UserId userId, String type, Long fromTimeStamp,
                                                                               Integer limit, Integer offset) {
        def startKey = [userId.toString(), type, fromTimeStamp]
        def endKey = [userId.toString(), type]
        return queryView('by_user_id_credential_type_time', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<List<UserCredentialVerifyAttempt>> searchByIPAddressAndCredentialTypeAndInterval(String ipAddress, String type, Long fromTimeStamp,
                                                                                  Integer limit, Integer offset) {
        def startKey = [ipAddress, type, fromTimeStamp]
        def endKey = [ipAddress, type]
        return queryView('by_ip_address_credential_type_time', startKey.toArray(new String()), endKey.toArray(new String()), true, limit, offset, true)
    }

    @Override
    Promise<Void> delete(UserCredentialVerifyAttemptId id) {
        return cloudantDelete(id.toString())
    }
}
