package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeRepositoryCloudantImpl extends CloudantClient<PIType> implements PITypeRepository {

    @Override
    Promise<PIType> create(PIType model) {
        return cloudantPost(model)
    }

    @Override
    Promise<PIType> update(PIType model, PIType oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<PIType> get(PITypeId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<List<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset) {
        return queryView('by_typeCode', typeCode, limit, offset, false)
    }

    @Override
    Promise<List<PIType>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }
}
