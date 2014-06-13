package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
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
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<PIType> create(PIType model) {
        return super.cloudantPost(model)
    }

    @Override
    Promise<PIType> update(PIType model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<PIType> get(PITypeId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        return super.cloudantDelete(id.toString())
    }

    @Override
    Promise<List<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset) {
        return super.queryView('by_typeCode', typeCode, limit, offset, false)
    }

    @Override
    Promise<List<PIType>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll()
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_typeCode': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.typeCode, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )
}
