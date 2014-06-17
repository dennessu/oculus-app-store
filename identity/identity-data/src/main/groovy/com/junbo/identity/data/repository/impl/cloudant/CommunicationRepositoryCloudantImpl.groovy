package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationRepositoryCloudantImpl extends CloudantClient<Communication> implements CommunicationRepository {

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<Communication> create(Communication model) {
        return super.cloudantPost(model)
    }

    @Override
    Promise<Communication> update(Communication model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<Communication> get(CommunicationId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        return super.cloudantDelete(id.toString())
    }

    @Override
    Promise<List<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset) {
        return super.queryView('by_translation', translation.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset) {
        return super.queryView('by_region', region.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit, Integer offset) {
        return super.queryView('by_region_and_translation', "${region.value}:${translation.value}", limit, offset, false)
    }

    @Override
    Promise<List<Communication>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll(limit, offset, false)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_region': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'if (doc.regions) {' +
                                        'for (var i in doc.regions) {' +
                                            'emit(doc.regions[i], doc._id)' +
                                        '}' +
                                    '}' +
                                    '}',
                            resultClass: String),
                    'by_translation': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'if (doc.translations) {' +
                                        'for (var i in doc.translations) {' +
                                            'emit(doc.translations[i], doc._id)' +
                                        '}' +
                                    '}' +
                                    '}',
                            resultClass: String),
                    'by_region_and_translation': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'if (doc.translations && doc.regions) {' +
                                        'for (var i in doc.regions) {' +
                                            'for (var j in doc.translations) {' +
                                                'emit(doc.regions[i] + \':\' + doc.translations[j], doc._id)' +
                                            '}' +
                                        '}' +
                                    '}' +
                                    '}' ,
                            resultClass: String)
            ]
    )
}
