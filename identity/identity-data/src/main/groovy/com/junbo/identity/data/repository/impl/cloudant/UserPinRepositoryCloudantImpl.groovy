package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPinId
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserPinRepositoryCloudantImpl extends CloudantClient<UserPin> implements UserPinRepository {

    @Override
    Promise<UserPin> create(UserPin userPin) {
        userPin.value = null
        return cloudantPost(userPin)
    }

    @Override
    Promise<UserPin> update(UserPin userPin) {
        userPin.value = null
        return cloudantPut(userPin)
    }

    @Override
    Promise<UserPin> get(UserPinId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        def startKey = [userId.toString(), active]
        def endKey = [userId.toString(), active]
        return queryView('by_user_id_active_status', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
        return cloudantDelete(id.toString())
    }
}
