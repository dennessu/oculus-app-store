package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class CountryRepositoryCloudantImpl extends CloudantClient<Country> implements CountryRepository {

    @Override
    Promise<Country> create(Country model) {
        if (model.id == null) {
            model.id = new CountryId(model.countryCode)
        }
        return cloudantPost(model).then { Country country ->
            return Promise.pure(country)
        }
    }

    @Override
    Promise<Country> update(Country model, Country oldModel) {
        return cloudantPut(model, oldModel).then { Country country ->
            return Promise.pure(country)
        }
    }

    @Override
    Promise<Country> get(CountryId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CountryId id) {
        return cloudantDelete(id.toString()).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset) {
        return queryView('by_default_currency', currencyId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset) {
        return queryView('by_default_locale', localeId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit, Integer offset) {
        def startKey = [currencyId.toString(), localeId.toString()]
        def endKey = [currencyId.toString(), localeId.toString()]
        return queryView('by_default_locale_currency', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }
}
