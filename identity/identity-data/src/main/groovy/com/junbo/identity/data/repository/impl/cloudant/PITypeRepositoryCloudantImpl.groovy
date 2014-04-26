package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
class PITypeRepositoryCloudantImpl extends CloudantClient<PIType> implements PITypeRepository {
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<PIType> create(PIType model) {
        if (model.id == null) {
            model.id = new PITypeId(idGenerator.nextIdByShardId(0))
        }

        return Promise.pure((PIType)super.cloudantPost(model))
    }

    @Override
    Promise<PIType> update(PIType model) {
        return Promise.pure((PIType)super.cloudantPut(model))
    }

    @Override
    Promise<PIType> get(PITypeId id) {
        return Promise.pure((PITypeId)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<PIType>> search(PITypeListOptions options) {
        return Promise.pure(super.cloudantGetAll())
    }
}
