package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.common.util.CountryCode
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.CountryLocaleKey
import com.junbo.identity.spec.v1.model.SubCountryLocaleKey
import com.junbo.identity.spec.v1.model.SubCountryLocaleKeys
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Check minimum and maximum subCountry's shortName length
 * Check minimum and maximum subCountry's longName length
 * Check minimum and maximum localeKey's value length
 * Check locales's allowed keys
 * Check countryCode is valid
 * Check defaultLocale id valid
 * Check defaultCurrency id valid
 * Check supportedLocales's locale id valid
 * Check no duplicate countryCode
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CountryValidatorImpl implements CountryValidator {

    private CountryRepository countryRepository
    private LocaleRepository localeRepository
    private CurrencyRepository currencyRepository

    private Integer minSubCountryShortNameLength
    private Integer maxSubCountryShortNameLength

    private Integer minSubCountryLongNameLength
    private Integer maxSubCountryLongNameLength

    private Integer minCountryShortNameLength
    private Integer maxCountryShortNameLength

    private Integer minCountryLongNameLength
    private Integer maxCountryLongNameLength

    @Required
    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
    }

    @Required
    void setCurrencyRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository
    }

    @Required
    void setMinSubCountryShortNameLength(Integer minSubCountryShortNameLength) {
        this.minSubCountryShortNameLength = minSubCountryShortNameLength
    }

    @Required
    void setMaxSubCountryShortNameLength(Integer maxSubCountryShortNameLength) {
        this.maxSubCountryShortNameLength = maxSubCountryShortNameLength
    }

    @Required
    void setMinSubCountryLongNameLength(Integer minSubCountryLongNameLength) {
        this.minSubCountryLongNameLength = minSubCountryLongNameLength
    }

    @Required
    void setMaxSubCountryLongNameLength(Integer maxSubCountryLongNameLength) {
        this.maxSubCountryLongNameLength = maxSubCountryLongNameLength
    }

    @Required
    void setMinCountryShortNameLength(Integer minCountryShortNameLength) {
        this.minCountryShortNameLength = minCountryShortNameLength
    }

    @Required
    void setMaxCountryShortNameLength(Integer maxCountryShortNameLength) {
        this.maxCountryShortNameLength = maxCountryShortNameLength
    }

    @Required
    void setMinCountryLongNameLength(Integer minCountryLongNameLength) {
        this.minCountryLongNameLength = minCountryLongNameLength
    }

    @Required
    void setMaxCountryLongNameLength(Integer maxCountryLongNameLength) {
        this.maxCountryLongNameLength = maxCountryLongNameLength
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

        if (options.returnLocale == null && !StringUtils.isEmpty(options.sortBy)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('locale').exception()
        }

        if (options.returnLocale != null && StringUtils.isEmpty(options.sortBy)) {
            throw AppCommonErrors.INSTANCE.parameterRequired('sortBy').exception()
        }

        if (!StringUtils.isEmpty(options.sortBy) && !options.sortBy.equalsIgnoreCase('shortName')) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('sortBy', 'sortBy only support shortName').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Country country) {
        if (country.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return checkBasicCountryInfo(country).then {
            return countryRepository.get(new CountryId(country.countryCode)).then { Country existing ->
                if (existing != null) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('countryCode').exception()
                }

                return Promise.pure(null)
            }
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
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (countryId != oldCountry.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (country.countryCode != oldCountry.countryCode) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('countryCode').exception()
        }

        return checkBasicCountryInfo(country)
    }

    private Promise<Void> checkBasicCountryInfo(Country country) {
        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (country.countryCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('countryCode').exception()
        }

        if (!ValidatorUtil.isValidCountryCode(country.countryCode)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('countryCode', CountryCode.values().join(',')).exception()
        }

        return checkDefaultLocale(country).then {
            return checkDefaultCurrency(country)
        }.then {
            return checkRatingBoards(country)
        }.then {
            return checkSubCountries(country)
        }.then {
            return checkSupportedLocales(country)
        }.then {
            return checkCountryLocaleKeys(country)
        }
    }

    private Promise<Void> checkDefaultLocale(Country country) {
        if (country.defaultLocale == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('defaultLocale').exception()
        }

        return localeRepository.get(country.defaultLocale).then { com.junbo.identity.spec.v1.model.Locale locale ->
            if (locale == null) {
                throw AppErrors.INSTANCE.localeNotFound(country.defaultLocale).exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkDefaultCurrency(Country country) {
        if (country.defaultCurrency == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('defaultCurrency').exception()
        }

        return currencyRepository.get(country.defaultCurrency).then {
            com.junbo.identity.spec.v1.model.Currency currency ->
                if (currency == null) {
                    throw AppErrors.INSTANCE.currencyNotFound(country.defaultCurrency).exception()
                }

                return Promise.pure(null)
        }
    }

    private Promise<Void> checkRatingBoards(Country country) {
        // Will accept all rating Boards input. It is oculus's resource. Will always trust this.
        if (country.ratingBoards != null) {
            return Promise.pure(null)
        }

        return Promise.pure(null)
    }

    private Promise<Void> checkSubCountries(Country country) {
        if (country.subCountries == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('subCountries').exception()
        }

        country.subCountries.each { Map.Entry<String, SubCountryLocaleKeys> subCountryEntry ->
            if (StringUtils.isEmpty(subCountryEntry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('subCountries.key', 'key can\'t be null or empty').exception()
            }

            checkSubCountryLocaleKeys(subCountryEntry.value)
        }

        return Promise.pure(null)
    }

    private Promise<Void> checkSupportedLocales(Country country) {
        if (country.supportedLocales == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('supportedLocales').exception()
        }

        Collection<LocaleId> localeIdList = country.supportedLocales.unique { LocaleId localeId ->
            return localeId
        }

        if (localeIdList.size() != country.supportedLocales.size()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('supportedLocales', 'Duplicate localeId found.').exception()
        }

        return Promise.each(country.supportedLocales) { LocaleId localeId ->
            return localeRepository.get(localeId).then { com.junbo.identity.spec.v1.model.Locale locale ->
                if (locale == null) {
                    throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkCountryLocaleKeys(Country country) {
        if (country.locales == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locales').exception()
        }

        country.locales.locales.each { Map.Entry<String, CountryLocaleKey> entry ->
            if (StringUtils.isEmpty(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales.key', 'locales.key can\'t be null or empty').exception()
            }

            CountryLocaleKey value = entry.value
            if (value == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value').exception()
            }
            if (value.shortName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value.shortName').exception()
            }
            if (value.shortName.length() > maxCountryShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('locales.value.shortName', maxCountryShortNameLength).exception()
            }
            if (value.shortName.length() < minCountryShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('locales.value.shortName', minCountryShortNameLength).exception()
            }

            if (value.longName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value.longName').exception()
            }
            if (value.longName.length() > maxCountryLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('locales.value.longName', maxCountryLongNameLength).exception()
            }
            if (value.longName.length() < minCountryLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('locales.value.longName', minCountryLongNameLength).exception()
            }
        }

        return Promise.pure(null)
    }

    private void checkSubCountryLocaleKeys(SubCountryLocaleKeys locales) {
        if (locales == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('locales', 'subCountries is null').exception()
        }
        if (locales.locales == null || locales.locales.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('locales', 'subCountries can\'t be empty').exception()
        }

        locales.locales.each { Map.Entry<String, SubCountryLocaleKey> localeKeyEntry ->
            if (localeKeyEntry.value == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('subCountries.locales').exception()
            }

            SubCountryLocaleKey localeKey = localeKeyEntry.value

            if (localeKey.shortName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('subCountries.locales.shortName').exception()
            }
            if (localeKey.shortName.length() > maxSubCountryShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('subCountries.locales.shortName', maxSubCountryShortNameLength).exception()
            }
            if (localeKey.shortName.length() < minSubCountryShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('subCountries.locales.shortName', minSubCountryShortNameLength).exception()
            }

            if (localeKey.longName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('subCountries.locales.longName').exception()
            }
            if (localeKey.longName.length() > maxSubCountryLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('subCountries.locales.longName', maxSubCountryLongNameLength).exception()
            }
            if (localeKey.longName.length() < minSubCountryLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('subCountries.locales.longName', minSubCountryLongNameLength).exception()
            }
        }
    }
}
