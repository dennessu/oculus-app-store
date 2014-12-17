package com.junbo.identity.data.repository

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by minhao on 4/24/14.
 */
public interface CountryRepository extends BaseRepository<Country, CountryId> {

    @ReadMethod
    Promise<Results<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit,
                                                                Integer offset)

    @ReadMethod
    Promise<Results<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<Country>> searchAll(Integer limit, Integer offset)
}