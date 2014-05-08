package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserTeleBackupCodeId
import com.junbo.identity.data.repository.UserTeleBackupCodeRepository
import com.junbo.identity.spec.v1.model.UserTeleBackupCode
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeRepositoryCloudantImpl extends CloudantClient<UserTeleBackupCode>
        implements UserTeleBackupCodeRepository  {

    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserTeleBackupCode>> search(UserTeleBackupCodeListOptions listOptions) {
        if (listOptions.active == null) {
            def list = super.queryView('by_user_id', listOptions.userId.value.toString(),
                    listOptions.limit, listOptions.offset, false)

            return Promise.pure(list)
        } else {
            def list = super.queryView('by_user_id_active',
                    "${listOptions.userId.value.toString()}:${listOptions.active.toString()}", listOptions.limit,
                    listOptions.offset, false)

            return Promise.pure(list)
        }
    }

    @Override
    Promise<UserTeleBackupCode> create(UserTeleBackupCode entity) {
        if (entity.id == null) {
            entity.id = new UserTeleBackupCodeId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTeleBackupCode)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTeleBackupCode> update(UserTeleBackupCode entity) {
        return Promise.pure((UserTeleBackupCode)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTeleBackupCode> get(UserTeleBackupCodeId id) {
        return Promise.pure((UserTeleBackupCode)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserTeleBackupCodeId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_active': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString() + \':\' + doc.active.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
