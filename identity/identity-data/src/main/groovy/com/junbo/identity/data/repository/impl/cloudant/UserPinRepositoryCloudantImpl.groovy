package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserPinId
import com.junbo.identity.data.repository.UserPinRepository
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.option.list.UserPinListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserPinRepositoryCloudantImpl extends CloudantClient<UserPin> implements UserPinRepository {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserPin> create(UserPin userPin) {
        if (userPin.id == null) {
            userPin.id = new UserPinId(idGenerator.nextId(userPin.userId.value))
        }
        userPin.value = null
        return Promise.pure((UserPin)super.cloudantPost(userPin))
    }

    @Override
    Promise<UserPin> update(UserPin userPin) {
        userPin.value = null
        return Promise.pure((UserPin)super.cloudantPut(userPin))
    }

    @Override
    Promise<UserPin> get(UserPinId id) {
        return Promise.pure((UserPin)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserPin>> search(UserPinListOptions getOption) {
        def list = super.queryView('by_user_id', getOption.userId.value.toString())
        if (getOption.active != null) {
            list.retainAll { UserPin element ->
                element.active == getOption.active
            }
        }

        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserPinId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
