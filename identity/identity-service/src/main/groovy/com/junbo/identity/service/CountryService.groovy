package com.junbo.identity.service

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface CountryService {
    Promise<Country> get(CountryId id)

    Promise<Country> create(Country model)

    Promise<Country> update(Country model, Country oldModel)

    Promise<Void> delete(CountryId id)

    Promise<Results<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit,
                                                                Integer offset)

    Promise<Results<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset)

    Promise<Results<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset)

    Promise<Results<Country>> searchAll(Integer limit, Integer offset)
}