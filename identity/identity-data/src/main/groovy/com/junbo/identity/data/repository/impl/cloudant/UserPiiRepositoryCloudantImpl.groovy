package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserPiiId
import com.junbo.identity.data.repository.UserPiiRepository
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-11.
 */
class UserPiiRepositoryCloudantImpl extends CloudantClient<UserPii> implements UserPiiRepository {
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
    Promise<UserPii> create(UserPii userPii) {
        if (userPii.id == null) {
            userPii.id = new UserPiiId(idGenerator.nextId(userPii.userId.value))
        }
        super.cloudantPost(userPii)
        return get((UserPiiId)userPii.id)
    }

    @Override
    Promise<UserPii> update(UserPii userPii) {
        super.cloudantPut(userPii)
        return get((UserPiiId)userPii.id)
    }

    @Override
    Promise<UserPii> get(UserPiiId userPiiId) {
        return Promise.pure((UserPii)super.cloudantGet(userPiiId.toString()))
    }

    @Override
    Promise<Void> delete(UserPiiId userPiiId) {
        super.cloudantDelete(userPiiId.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserPii>> search(UserPiiListOptions options) {
        def list = []
        if (options.userId != null) {
            list = super.queryView('by_user_id', options.userId.toString(), options.limit, options.offset, false)
        }
        else if (options.email != null) {
            list = super.queryView('by_email', options.email, options.limit, options.offset, false)
        }

        return Promise.pure(list)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_email': new CloudantViews.CloudantView(
                            map:'function(doc) {' +
                                '  for (var key in doc.emails) {' +
                                '     emit(doc.emails[key].value, doc._id);' +
                                '  }' +
                                '}',
                            resultClass: String
                    )
            ]
    )
}
