package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.CommunicationRepository
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationRepositoryCloudantImpl extends CloudantClient<Communication> implements CommunicationRepository {

    @Override
    Promise<Communication> create(Communication model) {
        return cloudantPost(model)
    }

    @Override
    Promise<Communication> update(Communication model, Communication oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<Communication> get(CommunicationId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<Results<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset) {
        return queryViewResults('by_translation', translation.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset) {
        return queryViewResults('by_region', region.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit, Integer offset) {
        def startKey = [region.toString(), translation.toString()]
        def endKey = [region.toString(), translation.toString()]
        return super.queryViewResults('by_region_and_translation', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false)
    }

    @Override
    Promise<Results<Communication>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false, true)
    }
}
