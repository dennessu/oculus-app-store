package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserPasswordId
import com.junbo.identity.data.repository.UserPasswordRepository
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.v1.option.list.UserPasswordListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserPasswordRepositoryCloudantImpl extends CloudantClient<UserPassword> implements UserPasswordRepository {
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
    Promise<UserPassword> create(UserPassword userPassword) {
        userPassword.id = new UserPasswordId(idGenerator.nextId(userPassword.userId.value))
        super.cloudantPost(userPassword)
        return get((UserPasswordId)userPassword.id)
    }

    @Override
    Promise<UserPassword> update(UserPassword userPassword) {
        super.cloudantPut(userPassword)
        return get((UserPasswordId)userPassword.id)
    }

    @Override
    Promise<UserPassword> get(UserPasswordId id) {
        return Promise.pure((UserPassword)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserPassword>> search(UserPasswordListOptions getOption) {
        def list = super.queryView('by_user_id', getOption.userId.value.toString())
        if (getOption.active != null) {
            list.retainAll { UserPassword element ->
                element.active == getOption.active
            }
        }

        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserPasswordId id) {
        super.cloudantDelete(id.value)
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
