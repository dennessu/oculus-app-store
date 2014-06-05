package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.TosId
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class TosRepositoryCloudantImpl extends CloudantClient<Tos> implements TosRepository {
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
    Promise<Tos> get(TosId tosId) {
        return super.cloudantGet(tosId.toString())
    }

    @Override
    Promise<Tos> create(Tos tos) {
        if (tos.id == null) {
            tos.id = new TosId(idGenerator.nextId())
        }
        return super.cloudantPost(tos)
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        return super.cloudantDelete(tosId.toString())
    }

    @Override
    Promise<Tos> update(Tos model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<List<Tos>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll()
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }
}
