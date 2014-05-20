package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.common.util.CountryCode
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.SubCountry
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
    private LocaleRepository localeRepository
    private CurrencyRepository currencyRepository

    private Integer minSubCountryShortNameLength
    private Integer maxSubCountryShortNameLength

    private Integer minSubCountryLongNameLength
    private Integer maxSubCountryLongNameLength

    private Integer minLocaleKeyValueLength
    private Integer maxLocaleKeyValueLength

    private List<String> allowedLocaleKeys

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
    void setMinLocaleKeyValueLength(Integer minLocaleKeyValueLength) {
        this.minLocaleKeyValueLength = minLocaleKeyValueLength
    }

    @Required
    void setMaxLocaleKeyValueLength(Integer maxLocaleKeyValueLength) {
        this.maxLocaleKeyValueLength = maxLocaleKeyValueLength
    }

    @Required
    void setAllowedLocaleKeys(List<String> allowedLocaleKeys) {
        this.allowedLocaleKeys = allowedLocaleKeys
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
        if (country.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return checkBasicCountryInfo(country).then {
            return countryRepository.get(new CountryId(country.countryCode)).then { Country existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('countryCode').exception()
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
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (countryId != oldCountry.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (country.countryCode != oldCountry.countryCode) {
            throw AppErrors.INSTANCE.fieldInvalid('countryCode').exception()
        }

        return checkBasicCountryInfo(country)
    }

    private Promise<Void> checkBasicCountryInfo(Country country) {
        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (country.countryCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('countryCode').exception()
        }

        if (!ValidatorUtil.isValidCountryCode(country.countryCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('countryCode', CountryCode.values().join(',')).exception()
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
            return checkLocaleKeys(country)
        }
    }

    private Promise<Void> checkDefaultLocale(Country country) {
        if (country.defaultLocale == null) {
            throw AppErrors.INSTANCE.fieldRequired('defaultLocale').exception()
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
            throw AppErrors.INSTANCE.fieldRequired('defaultCurrency').exception()
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
            throw AppErrors.INSTANCE.fieldRequired('subCountries').exception()
        }

        country.subCountries.each { Map.Entry<String, SubCountry> subCountryEntry ->
            SubCountry subCountry = subCountryEntry.value

            if (subCountry.shortNameKey == null) {
                throw AppErrors.INSTANCE.fieldRequired('subCountries.shortNameKey').exception()
            }
            if (subCountry.shortNameKey.length() > maxSubCountryShortNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('subCountries.shortNameKey', maxSubCountryShortNameLength).
                        exception()
            }
            if (subCountry.shortNameKey.length() < minSubCountryLongNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('subCountries.shortNameKey', minSubCountryLongNameLength).
                        exception()
            }

            if (subCountry.longNameKey == null) {
                throw AppErrors.INSTANCE.fieldRequired('subCountries.longNameKey').exception()
            }
            if (subCountry.longNameKey.length() > maxSubCountryLongNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('subCountries.longNameKey', maxSubCountryLongNameLength).
                        exception()
            }
            if (subCountry.longNameKey.length() < minSubCountryLongNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('subCountries.longNameKey', minSubCountryLongNameLength).
                        exception()
            }
        }

        return Promise.pure(null)
    }

    private Promise<Void> checkSupportedLocales(Country country) {
        if (country.supportedLocales == null) {
            throw AppErrors.INSTANCE.fieldRequired('supportedLocales').exception()
        }

        Collection<LocaleId> localeIdList = country.supportedLocales.unique { LocaleId localeId ->
            return localeId
        }

        if (localeIdList.size() != country.supportedLocales.size()) {
            throw AppErrors.INSTANCE.fieldInvalidException('supportedLocales', 'Duplicate localeId found.').exception()
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

    private Promise<Void> checkLocaleKeys(Country country) {
        if (country.locales == null) {
            throw AppErrors.INSTANCE.fieldRequired('localeKeys').exception()
        }

        country.locales.each { Map.Entry<String, String> entry ->
            String key = entry.key
            if (!(key in allowedLocaleKeys)) {
                throw AppErrors.INSTANCE.fieldInvalid('localeKeys', allowedLocaleKeys.join(',')).exception()
            }

            String value = entry.value
            if (value == null) {
                throw AppErrors.INSTANCE.fieldRequired('localeKeys.value').exception()
            }
            if (value.length() > maxLocaleKeyValueLength) {
                throw AppErrors.INSTANCE.fieldTooLong('localeKeys.value', maxLocaleKeyValueLength).exception()
            }
            if (value.length() < minLocaleKeyValueLength) {
                throw AppErrors.INSTANCE.fieldTooShort('localeKeys.value', minLocaleKeyValueLength).exception()
            }
        }

        return Promise.pure(null)
    }
}
