package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.data.repository.UserTosRepository
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 4/13/14.
 */
@CompileStatic
class UserTosRepositoryCloudantImpl extends CloudantClient<UserTosAgreement> implements UserTosRepository {
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
    Promise<UserTosAgreement> create(UserTosAgreement entity) {
        if (entity.id == null) {
            entity.id = new UserTosAgreementId(idGenerator.nextId(entity.userId.value))
        }
        return Promise.pure((UserTosAgreement)super.cloudantPost(entity))
    }

    @Override
    Promise<UserTosAgreement> update(UserTosAgreement entity) {
        return Promise.pure((UserTosAgreement)super.cloudantPut(entity))
    }

    @Override
    Promise<UserTosAgreement> get(UserTosAgreementId id) {
        return Promise.pure((UserTosAgreement)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<List<UserTosAgreement>> search(UserTosAgreementListOptions getOption) {
        def list = super.queryView('by_user_id', getOption.userId.value.toString(),
                getOption.limit, getOption.offset, false)
        if (getOption.tosId != null) {
            list.retainAll { UserTosAgreement agreement ->
                agreement.tosId == getOption.tosId
            }
        }
        return Promise.pure(list)
    }

    @Override
    Promise<Void> delete(UserTosAgreementId id) {
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
