package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.CommunicationId
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationRepositoryCloudantImpl extends CloudantClient<Communication> implements CommunicationRepository {

    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<Communication> searchByName(String name) {
        def list = super.queryView('by_name', name)
        return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
    }

    @Override
    Promise<Communication> create(Communication model) {
        model.id = new CommunicationId(idGenerator.nextIdByShardId(shardAlgorithm.shardId()))

        return Promise.pure((Communication)super.cloudantPost(model))
    }

    @Override
    Promise<Communication> update(Communication model) {
        return Promise.pure((Communication)super.cloudantPut(model))
    }

    @Override
    Promise<Communication> get(CommunicationId id) {
        return Promise.pure((Communication)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.name, doc._id)' +
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
