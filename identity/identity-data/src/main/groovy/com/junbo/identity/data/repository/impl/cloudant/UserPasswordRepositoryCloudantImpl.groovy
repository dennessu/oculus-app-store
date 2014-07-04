package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserPasswordRepositoryCloudantImpl extends CloudantClient<UserPassword> implements UserPasswordRepository {

    @Override
    Promise<UserPassword> create(UserPassword userPassword) {
        userPassword.value = null
        return cloudantPost(userPassword)
    }

    @Override
    Promise<UserPassword> update(UserPassword userPassword) {
        userPassword.value = null
        return cloudantPut(userPassword)
    }

    @Override
    Promise<UserPassword> get(UserPasswordId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserPassword>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserPassword>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                              Integer offset) {
        def startKey = [userId.toString(), active]
        def endKey = [userId.toString(), active]
        return queryView('by_user_id_active_status', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserPasswordId id) {
        return cloudantDelete(id.toString())
    }
}
