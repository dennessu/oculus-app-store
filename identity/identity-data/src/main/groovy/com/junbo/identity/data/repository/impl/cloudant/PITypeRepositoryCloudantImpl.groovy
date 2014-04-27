package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.PITypeId
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeRepositoryCloudantImpl extends CloudantClient<PIType> implements PITypeRepository {
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    protected CloudantViews getCloudantViews() {
        return views
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
        return Promise.pure((PIType)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(PITypeId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    @Override
    Promise<List<PIType>> search(PITypeListOptions options) {
        if (options.typeCode != null) {
            def list = super.queryView('by_typeCode', options.typeCode)
            return list.size() > 0 ? Promise.pure(list[0]) : Promise.pure(null)
        }

        return Promise.pure(super.cloudantGetAll())
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
