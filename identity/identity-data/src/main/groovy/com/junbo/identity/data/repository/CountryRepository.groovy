package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by minhao on 4/24/14.
 */
public interface CountryRepository extends BaseRepository<Country, CountryId> {
    @ReadMethod
    Promise<List<Country>> search(CountryListOptions options)
}