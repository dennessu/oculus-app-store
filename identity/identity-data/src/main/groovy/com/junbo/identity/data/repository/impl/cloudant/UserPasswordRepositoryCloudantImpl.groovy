package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
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
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserPassword> create(UserPassword userPassword) {
        userPassword.value = null
        return super.cloudantPost(userPassword)
    }

    @Override
    Promise<UserPassword> update(UserPassword userPassword) {
        userPassword.value = null
        return super.cloudantPut(userPassword)
    }

    @Override
    Promise<UserPassword> get(UserPasswordId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserPassword>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserPassword>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit,
                                                              Integer offset) {
        return super.queryView('by_user_id_active_status', "${userId.toString()}:${active}", limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserPasswordId id) {
        return super.cloudantDelete(id.toString())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_active_status': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.active, doc._id)' +
                                 '}',
                            resultClass: String)
            ]
    )
}
