package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-4-25.
 */
class PITypeRepositoryCloudantImpl extends CloudantClient<PIType> implements PITypeRepository {
    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<List<PIType>> getAllPIType() {
        return Promise.pure(super.cloudantGetAll())
    }

    @Override
    Promise<PIType> create(PIType model) {
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
}
