package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CountryId
import com.junbo.identity.core.service.util.ValidatorUtil
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CountryValidatorImpl implements CountryValidator {

    private CountryRepository countryRepository

    @Required
    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }

    @Override
    Promise<Country> validateForGet(CountryId countryId) {
        if (countryId == null || countryId.value == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        return countryRepository.get(countryId).then { Country country ->
            if (country == null) {
                throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
            }

            return Promise.pure(country)
        }
    }

    @Override
    Promise<Void> validateForSearch(CountryListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Country country) {
        checkBasicCountryInfo(country)

        if (country.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return countryRepository.get(new CountryId(country.countryCode)).then { Country existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('countryCode').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(CountryId countryId, Country country, Country oldCountry) {
        if (countryId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (countryId != country.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (countryId != oldCountry.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicCountryInfo(country)

        if (country.countryCode != oldCountry.countryCode) {
            throw AppErrors.INSTANCE.fieldInvalid('countryCode').exception()
        }

        return Promise.pure(null)
    }

    private void checkBasicCountryInfo(Country country) {
        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (country.countryCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('countryCode').exception()
        }
        if (country.defaultLocale == null) {
            throw AppErrors.INSTANCE.fieldRequired('defaultLocale').exception()
        }
        if (country.defaultCurrency == null) {
            throw AppErrors.INSTANCE.fieldRequired('defaultCurrency').exception()
        }
        if (country.subCountries == null) {
            throw AppErrors.INSTANCE.fieldRequired('subCountries').exception()
        }
        if (country.supportedLocales == null) {
            throw AppErrors.INSTANCE.fieldRequired('supportedLocales').exception()
        }
        if (country.locales == null) {
            throw AppErrors.INSTANCE.fieldRequired('locales').exception()
        }
        if (country.futureExpansion == null) {
            throw AppErrors.INSTANCE.fieldRequired('futureExpansion').exception()
        }
        if (!ValidatorUtil.isValidCountryCode(country.countryCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('countryCode').exception()
        }
    }
}
