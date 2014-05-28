package com.junbo.identity.data.repository.impl.sql

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
class CommunicationRepositorySqlImpl implements CommunicationRepository {

    @Override
    Promise<List<Communication>> search(CommunicationListOptions options) {
        return null
    }

    @Override
    Promise<Communication> create(Communication model) {
        return null
    }

    @Override
    Promise<Communication> update(Communication model) {
        return null
    }

    @Override
    Promise<Communication> get(CommunicationId id) {
        return null
    }

    @Override
    Promise<Void> delete(CommunicationId id) {
        return null
    }

    @Override
    Promise<List<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit, Integer offset) {
        return null
    }

    @Override
    Promise<List<Communication>> searchAll(Integer limit, Integer offset) {
        return null
    }
}
