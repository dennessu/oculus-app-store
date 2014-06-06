package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CountryId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.CountryFilter
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class CountryResourceImpl implements CountryResource {
    private static final String IDENTITY_ADMIN_SCOPE = 'identity.admin'

    @Autowired
    private CountryRepository countryRepository

    @Autowired
    private CountryFilter countryFilter

    @Autowired
    private CountryValidator countryValidator

    @Override
    Promise<Country> create(Country country) {
        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        country = countryFilter.filterForCreate(country)

        return countryValidator.validateForCreate(country).then {
            return countryRepository.create(country).then { Country newCountry ->
                Created201Marker.mark(newCountry.id)

                newCountry = countryFilter.filterForGet(newCountry, null)
                return Promise.pure(newCountry)
            }
        }
    }

    @Override
    Promise<Country> put(CountryId countryId, Country country) {
        if (countryId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        return countryRepository.get(countryId).then { Country oldCountry ->
            if (oldCountry == null) {
                throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
            }

            country = countryFilter.filterForPut(country, oldCountry)

            return countryValidator.validateForUpdate(countryId, country, oldCountry).then {
                return countryRepository.update(country).then { Country newCountry ->
                    newCountry = countryFilter.filterForGet(newCountry, null)
                    return Promise.pure(newCountry)
                }
            }
        }
    }

    @Override
    Promise<Country> patch(CountryId countryId, Country country) {
        if (countryId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        return countryRepository.get(countryId).then { Country oldCountry ->
            if (oldCountry == null) {
                throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
            }

            country = countryFilter.filterForPatch(country, oldCountry)

            return countryValidator.validateForUpdate(
                    countryId, country, oldCountry).then {
                return countryRepository.update(country).then { Country newCountry ->
                    newCountry = countryFilter.filterForGet(newCountry, null)
                    return Promise.pure(newCountry)
                }
            }
        }
    }

    @Override
    Promise<Country> get(CountryId countryId, CountryGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return countryValidator.validateForGet(countryId).then {
            return countryRepository.get(countryId).then { Country newCountry ->
                if (newCountry == null) {
                    throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
                }

                newCountry = countryFilter.filterForGet(newCountry, null)
                return Promise.pure(newCountry)
            }
        }
    }

    @Override
    Promise<Results<Country>> list(CountryListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return countryValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<Country> countryList ->
                def result = new Results<Country>(items: [])

                countryList.each { Country newCountry ->
                    newCountry = countryFilter.filterForGet(newCountry, null)

                    if (newCountry != null) {
                        result.items.add(newCountry)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Void> delete(CountryId countryId) {
        if (countryId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        return countryValidator.validateForGet(countryId).then {
            return countryRepository.delete(countryId)
        }
    }

    private Promise<List<Country>> search(CountryListOptions countryListOptions) {
        if (countryListOptions.currencyId != null && countryListOptions.localeId != null) {
            return countryRepository.searchByDefaultCurrencyIdAndLocaleId(countryListOptions.currencyId,
                    countryListOptions.localeId, countryListOptions.limit, countryListOptions.offset)
        } else if (countryListOptions.currencyId != null) {
            return countryRepository.searchByDefaultCurrencyId(countryListOptions.currencyId, countryListOptions.limit,
                    countryListOptions.offset)
        } else if (countryListOptions.localeId != null) {
            return countryRepository.searchByDefaultLocaleId(countryListOptions.localeId, countryListOptions.limit,
                    countryListOptions.offset)
        } else {
            return countryRepository.searchAll(countryListOptions.limit, countryListOptions.offset)
        }
    }
}
