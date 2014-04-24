package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserTeleId
import com.junbo.identity.data.repository.UserTeleRepository
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleRepositoryCloudantImpl extends CloudantClient<UserTeleCode> implements UserTeleRepository  {
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<UserTeleCode> findActiveTeleCode(Long userId, String phoneNumber) {
        def list = super.queryView('by_user_id', userId.toString(),
                Integer.MAX_VALUE, 0, false)

        if (list == null) {
            return Promise.pure(null)
        }

        UserTeleCode result = (UserTeleCode)list.find { UserTeleCode userTeleCode ->
            if (userTeleCode.phoneNumber == phoneNumber && userTeleCode.expiresBy.after(new Date())) {
                return true
            }
        }

        return Promise.pure(result)
    }

    @Override
    Promise<UserTeleCode> create(UserTeleCode entity) {
        if (entity.id == null) {
            entity.id = new UserTeleId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTeleCode)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTeleCode> update(UserTeleCode entity) {
        return Promise.pure((UserTeleCode)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTeleCode> get(UserTeleId id) {
        return Promise.pure((UserTeleCode)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTeleId id) {
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

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
