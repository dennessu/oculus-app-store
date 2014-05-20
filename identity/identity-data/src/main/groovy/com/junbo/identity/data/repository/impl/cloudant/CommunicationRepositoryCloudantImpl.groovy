package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.CommunicationId
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationRepositoryCloudantImpl extends CloudantClient<Communication> implements CommunicationRepository {
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
    Promise<List<Communication>> search(CommunicationListOptions options) {
        if (options.region != null && options.translation != null) {
            List list = super.queryView('by_region', options.region.value)
            list.removeAll { Communication communication ->
                return !communication.translations.contains(options.translation)
            }

            return Promise.pure(list)
        }
        if (options.region != null) {
            return Promise.pure(super.queryView('by_region', options.region.value))
        }
        if (options.translation != null) {
            return Promise.pure(super.queryView('by_translation', options.translation.value))
        }

        return Promise.pure(super.cloudantGetAll())
    }

    @Override
    Promise<Communication> create(Communication model) {
        if (model.id == null) {
            // hard code to shard 0 for all communication resource
            model.id = new CommunicationId(idGenerator.nextIdByShardId(0))
        }

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
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    // Won't support translation_region to aovid translation * region view
    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_region': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'if (doc.regions) {' +
                                        'for (var i in doc.regions) {' +
                                            'emit(doc.regions[i].value, doc._id)' +
                                        '}' +
                                    '}' +
                                    '}',
                            resultClass: String),
                    'by_translation': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'if (doc.translations) {' +
                                        'for (var i in doc.translations) {' +
                                            'emit(doc.translations[i].value, doc._id)' +
                                        '}' +
                                    '}' +
                                    '}',
                            resultClass: String)
            ]
    )
}
