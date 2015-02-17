package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.id.PITypeId
import com.junbo.common.model.Results
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
        return cloudantPost(model).then { PIType piType ->
            return Promise.pure(piType)
        }
    }

    @Override
    Promise<PIType> update(PIType model, PIType oldModel) {
        return cloudantPut(model, oldModel).then { PIType piType ->
            return Promise.pure(piType)
        }
    }

    @Override
    Promise<PIType> get(PITypeId id) {
        return cloudantGet(id.toString()).then { PIType piType ->
            if (piType == null) {
                return Promise.pure(null)
            }

            return Promise.pure(piType)
        }
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        return cloudantDelete(id.toString()).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset) {
        return queryView('by_typeCode', typeCode, limit, offset, false)
    }

    @Override
    Promise<List<PIType>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset
        )).then { Results<PIType> results ->
            return Promise.pure(results.items)
        }
    }
}
