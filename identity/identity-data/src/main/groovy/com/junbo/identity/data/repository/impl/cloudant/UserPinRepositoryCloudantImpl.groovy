package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
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
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserPin> create(UserPin userPin) {
        userPin.value = null
        return super.cloudantPost(userPin)
    }

    @Override
    Promise<UserPin> update(UserPin userPin) {
        userPin.value = null
        return super.cloudantPut(userPin)
    }

    @Override
    Promise<UserPin> get(UserPinId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserPin>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserPin>> searchByUserIdAndActiveStatus(UserId userId, Boolean active, Integer limit, Integer offset) {
        return super.queryView('by_user_id_active_status', "${userId.toString()}:${active}", limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
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
