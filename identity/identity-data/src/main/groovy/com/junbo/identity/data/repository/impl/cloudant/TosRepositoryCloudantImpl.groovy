package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.TosId
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class TosRepositoryCloudantImpl extends CloudantClient<Tos> implements TosRepository {

    @Override
    Promise<Tos> get(TosId tosId) {
        return cloudantGet(tosId.toString())
    }

    @Override
    Promise<Tos> create(Tos tos) {
        return cloudantPost(tos)
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        return cloudantDelete(tosId.toString())
    }

    @Override
    Promise<Tos> update(Tos model, Tos oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<List<Tos>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }
}
