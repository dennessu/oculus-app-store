package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.CommunicationId
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
interface CommunicationRepository extends BaseRepository<Communication, CommunicationId> {
    @ReadMethod
    Promise<List<Communication>> searchByTranslation(LocaleId translation, Integer limit, Integer offset);

    @ReadMethod
    Promise<List<Communication>> searchByRegion(CountryId region, Integer limit, Integer offset);

    @ReadMethod
    Promise<List<Communication>> searchByRegionAndTranslation(CountryId region, LocaleId translation, Integer limit,
                                                              Integer offset);

    @ReadMethod
    Promise<List<Communication>> searchAll(Integer limit, Integer offset)
}
