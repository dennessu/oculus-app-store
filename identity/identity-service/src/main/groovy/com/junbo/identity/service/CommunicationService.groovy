package com.junbo.identity.service

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/20/14.
 */
@CompileStatic
interface CommunicationService {
    Promise<Communication> get(CommunicationId id);

    Promise<Communication> create(Communication model);

    Promise<Communication> update(Communication model, Communication oldModel);

    Promise<Void> delete(CommunicationId id);

    Promise<List<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset);

    Promise<List<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset);

    Promise<List<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit,
                                                              Integer offset);

    Promise<List<Communication>> searchAll(Integer limit, Integer offset)
}
