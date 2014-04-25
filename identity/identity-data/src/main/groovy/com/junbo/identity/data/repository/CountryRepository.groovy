package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by minhao on 4/24/14.
 */
public interface CountryRepository extends IdentityBaseRepository<Country, CountryId> {
    Promise<List<Country>> search(CountryListOptions options)
}