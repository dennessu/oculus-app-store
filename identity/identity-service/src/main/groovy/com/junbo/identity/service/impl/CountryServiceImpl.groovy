package com.junbo.identity.service.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.service.CountryService
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
class CountryServiceImpl implements CountryService {
    private CountryRepository countryRepository

    @Override
    Promise<Country> get(CountryId id) {
        return countryRepository.get(id)
    }

    @Override
    Promise<Country> create(Country model) {
        return countryRepository.create(model)
    }

    @Override
    Promise<Country> update(Country model, Country oldModel) {
        return countryRepository.update(model, oldModel)
    }

    @Override
    Promise<Void> delete(CountryId id) {
        return countryRepository.delete(id)
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit, Integer offset) {
        return countryRepository.searchByDefaultCurrencyIdAndLocaleId(currencyId, localeId, limit, offset)
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset) {
        return countryRepository.searchByDefaultCurrencyId(currencyId, limit, offset)
    }

    @Override
    Promise<List<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset) {
        return countryRepository.searchByDefaultLocaleId(localeId, limit, offset)
    }

    @Override
    Promise<List<Country>> searchAll(Integer limit, Integer offset) {
        return countryRepository.searchAll(limit, offset)
    }

    @Required
    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }
}
