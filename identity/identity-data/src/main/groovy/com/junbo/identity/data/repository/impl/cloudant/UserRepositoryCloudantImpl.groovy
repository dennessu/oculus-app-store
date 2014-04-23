package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserRepositoryCloudantImpl extends CloudantClient<User> implements UserRepository {
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

    protected CloudantViews views = new CloudantViews(
        views: [
            'by_canonical_username': new CloudantViews.CloudantView(
                map: 'function(doc) {' +
                        '  emit(doc.canonicalUsername, doc._id)' +
                        '}',
                resultClass: String)
        ]
    )

    @Override
    Promise<User> create(User user) {

        if (user.id == null) {
            user.id = new UserId(idGenerator.nextIdByShardId(shardAlgorithm.shardId()))
        }
        return Promise.pure((User)super.cloudantPost(user))
    }

    @Override
    Promise<User> update(User user) {
        return Promise.pure((User)super.cloudantPut(user))
    }

    @Override
    Promise<User> get(UserId userId) {
        return Promise.pure((User)super.cloudantGet(userId.toString()))
    }

    @Override
    Promise<Void> delete(UserId userId) {
        super.cloudantDelete(userId.value)
        return Promise.pure(null)
    }

    @Override
    Promise<User> getUserByCanonicalUsername(String canonicalUsername) {
        def list = super.queryView('by_canonical_username', canonicalUsername)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }
}
