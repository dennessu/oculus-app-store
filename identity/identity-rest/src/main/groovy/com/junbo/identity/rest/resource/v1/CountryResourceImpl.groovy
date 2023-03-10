package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.CountryFilter
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.data.identifiable.LocaleAccuracy
import com.junbo.identity.service.CountryService
import com.junbo.identity.service.LocaleService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response
import java.lang.reflect.Field

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class CountryResourceImpl implements CountryResource {
    private static Map<String, Field> fieldMap = new HashMap<String, Field>()
    private static final String SORT_BY_SHORT_NAME = 'shortName'

    @Autowired
    private CountryService countryService

    @Autowired
    private LocaleService localeService

    @Autowired
    private CountryFilter countryFilter

    @Autowired
    private CountryValidator countryValidator

    @Override
    Promise<Country> create(Country country) {
        if (country == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        country = countryFilter.filterForCreate(country)

        return countryValidator.validateForCreate(country).then {
            return countryService.create(country).then { Country newCountry ->
                Created201Marker.mark(newCountry.id)

                newCountry = countryFilter.filterForGet(newCountry, null)
                return Promise.pure(newCountry)
            }
        }
    }

    @Override
    Promise<Country> put(CountryId countryId, Country country) {
        if (countryId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (country == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return countryService.get(countryId).then { Country oldCountry ->
            if (oldCountry == null) {
                throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
            }

            country = countryFilter.filterForPut(country, oldCountry)

            return countryValidator.validateForUpdate(countryId, country, oldCountry).then {
                return countryService.update(country, oldCountry).then { Country newCountry ->
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

        return countryValidator.validateForGet(countryId).then { Country country ->
            return filterCountry(country, getOptions.locale).then { Country filterCountry ->
                filterCountry = countryFilter.filterForGet(filterCountry, getOptions.properties?.split(',') as List<String>)
                return Promise.pure(filterCountry)
            }
        }
    }

    @Override
    Promise<Results<Country>> list(CountryListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return countryValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { Results<Country> countryList ->
                def result = new Results<Country>(items: [])
                result.total = countryList.total

                return Promise.pure().then {
                    if (StringUtils.isEmpty(listOptions.returnLocale?.toString())) {
                        countryList.items.each { Country existing ->
                            existing = countryFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                            result.items.add(existing)
                        }

                        return Promise.pure(null)
                    }

                    return localeService.get(listOptions.returnLocale).then { Locale queryLocale ->
                        if (queryLocale == null) {
                            countryList.items.each { Country existing ->
                                existing = countryFilter.filterForGet(existing, listOptions.properties?.split(',') as List<String>)
                                result.items.add(existing)
                            }

                            return Promise.pure(null)
                        }

                        return Promise.each(countryList.items) { Country newCountry ->
                            return filterCountry(newCountry, listOptions.returnLocale?.toString()).then { Country filterCountry ->
                                if (filterCountry != null) {
                                    filterCountry = countryFilter.filterForGet(filterCountry, listOptions.properties?.split(',') as List<String>)
                                    result.items.add(filterCountry)
                                }

                                return Promise.pure(null)
                            }
                        }
                    }
                }.then {
                    if (listOptions.returnLocale != null && !StringUtils.isEmpty(listOptions.sortBy)) {
                        result.items.sort { Country temp ->
                            CountryLocaleKey countryLocaleKey = temp.locales?.get(listOptions.getReturnLocale().toString())

                            if (listOptions.sortBy.equalsIgnoreCase(SORT_BY_SHORT_NAME)) {
                                return countryLocaleKey?.shortName == null ? "" : countryLocaleKey?.shortName
                            } else {
                                return ''
                            }
                        }
                    }
                    return Promise.pure(result)
                }
            }
        }
    }

    @Override
    Promise<Response> delete(CountryId countryId) {
        if (countryId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return countryValidator.validateForGet(countryId).then {
            return countryService.delete(countryId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    private Promise<Results<Country>> search(CountryListOptions countryListOptions) {
        if (countryListOptions.currencyId != null && countryListOptions.localeId != null) {
            return countryService.searchByDefaultCurrencyIdAndLocaleId(countryListOptions.currencyId,
                    countryListOptions.localeId, countryListOptions.limit, countryListOptions.offset)
        } else if (countryListOptions.currencyId != null) {
            return countryService.searchByDefaultCurrencyId(countryListOptions.currencyId, countryListOptions.limit,
                    countryListOptions.offset)
        } else if (countryListOptions.localeId != null) {
            return countryService.searchByDefaultLocaleId(countryListOptions.localeId, countryListOptions.limit,
                    countryListOptions.offset)
        } else {
            return countryService.searchAll(countryListOptions.limit, countryListOptions.offset)
        }
    }

    private Promise<Country> filterCountry(Country country, String locale) {
        if (StringUtils.isEmpty(locale) || country == null) {
            return Promise.pure(country)
        }

        return localeService.get(new LocaleId(locale)).then { Locale inputLocale ->
            if (inputLocale == null) {
                return Promise.pure(country)
            }

            return filterSubCountries(country, locale).then { Country newCountry ->
                CountryLocaleKey localeKey = country.locales.get(locale)
                return filterCountryLocaleKeys(newCountry.locales, locale).then { Map<String, CountryLocaleKey> map ->
                    newCountry.locales = map
                    newCountry.localeAccuracy = calcCountryLocaleKeyAccuracy(localeKey, map.get(locale))
                    return Promise.pure(newCountry)
                }
            }
        }
    }

    private Promise<Country> filterSubCountries(Country country, String locale) {
        if (country.subCountries == null || country.subCountries.isEmpty()) {
            return Promise.pure(country)
        }

        return Promise.each(country.subCountries.entrySet()) { Map.Entry<String, SubCountryLocaleKeys> entry ->
            return filterSubCountryLocaleKeys(entry.value, locale).then { SubCountryLocaleKeys keys ->
                entry.value = keys
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(country)
        }
    }

    private Promise<SubCountryLocaleKeys> filterSubCountryLocaleKeys(SubCountryLocaleKeys keys, String locale) {
        SubCountryLocaleKey subCountryLocaleKey = keys.locales.get(locale)
        return filterSubCountryLocaleKeys(keys.locales, locale).then { Map<String, SubCountryLocaleKey> map ->
            keys.locales = map
            keys.localeAccuracy = calcSubCountryLocaleAccuracy(subCountryLocaleKey, map.get(locale))
            return Promise.pure(keys)
        }
    }

    static String calcSubCountryLocaleAccuracy(SubCountryLocaleKey source, SubCountryLocaleKey target) {
        if (source == target) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source == null && (target.shortName == null && target.longName == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (target == null && (source.shortName == null && source.longName == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (target == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (source.shortName == target.shortName && source.longName == target.longName) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source.shortName == target.shortName || source.longName == target.longName) {
            return LocaleAccuracy.MEDIUM.toString()
        } else {
            return LocaleAccuracy.LOW.toString()
        }
    }

    static String calcCountryLocaleKeyAccuracy(CountryLocaleKey source, CountryLocaleKey target) {
        if (source == target) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source == null && (target.shortName == null && target.longName == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source == null) {
            return LocaleAccuracy.LOW.toString()
        }

        if (target == null && (source.shortName == null && source.longName == null)) {
            return LocaleAccuracy.HIGH.toString()
        } else if (target == null) {
            return LocaleAccuracy.HIGH.toString()
        }

        if (source.shortName == target.shortName && source.longName == target.longName) {
            return LocaleAccuracy.HIGH.toString()
        } else if (source.shortName == target.shortName || source.longName == target.longName) {
            return LocaleAccuracy.MEDIUM.toString()
        } else {
            return LocaleAccuracy.LOW.toString()
        }
    }

    private Promise<Map<String, SubCountryLocaleKey>> filterSubCountryLocaleKeys(Map<String, SubCountryLocaleKey> localesKey, String locale) {
        SubCountryLocaleKey key = new SubCountryLocaleKey()

        Field[] fields = SubCountryLocaleKey.class.getDeclaredFields()
        return Promise.each(Arrays.asList(fields)) { Field field ->
            field = getAndCacheField(SubCountryLocaleKey.class, field.getName())
            return fillSubCountryLocaleKey(localesKey, field.getName(), locale).then { Map<String, Object> value ->
                if (value == null || value.isEmpty()) {
                    return Promise.pure(null)
                }

                value.each { Map.Entry<String, Object> entry ->
                    field.set(key, entry.value)
                }

                return Promise.pure(null)
            }
        }.then {
            Map<String, SubCountryLocaleKey> map = new HashMap<>()
            map.put(locale, key)
            return Promise.pure(map)
        }
    }

    private Promise<Map<String, CountryLocaleKey>> filterCountryLocaleKeys(Map<String, CountryLocaleKey> localesKey, String locale) {
        CountryLocaleKey key = new CountryLocaleKey()

        Field[] fields = CountryLocaleKey.class.getDeclaredFields()
        return Promise.each(Arrays.asList(fields)) { Field field ->
            field = getAndCacheField(CountryLocaleKey.class, field.getName())
            return fillCountryLocaleKey(localesKey, field.getName(), locale).then { Map<String, Object> value ->
                if (value == null || value.isEmpty()) {
                    return Promise.pure(null)
                }

                value.each { Map.Entry<String, Object> entry ->
                    field.set(key, entry.value)
                }

                return Promise.pure(null)
            }
        }.then {
            Map<String, CountryLocaleKey> map = new HashMap<>()
            map.put(locale, key)
            return Promise.pure(map)
        }
    }

    // return locale:fieldValue
    // if we can't find it in the end, will return error;
    // else will return the first locale with fieldValue
    private Promise<Map<String, Object>> fillSubCountryLocaleKey(Map<String, SubCountryLocaleKey> locales, String fieldName, String initLocale) {
        if (locales == null) {
            return Promise.pure(null)
        }
        SubCountryLocaleKey localeKey = locales.get(initLocale)

        if (localeKey != null) {
            Object obj = getAndCacheField(SubCountryLocaleKey.class, fieldName).get(localeKey)
            if (obj != null) {
                Map<String, Object> map = new HashMap<>()
                map.put(initLocale, obj)
                return Promise.pure(map)
            }
        }

        return localeService.get(new LocaleId(initLocale)).then { com.junbo.identity.spec.v1.model.Locale locale1 ->
            if (locale1 == null || locale1.fallbackLocale == null) {
                return Promise.pure(null)
            }

            return fillSubCountryLocaleKey(locales, fieldName, locale1.fallbackLocale.toString())
        }
    }

    private Promise<Map<String, Object>> fillCountryLocaleKey(Map<String, CountryLocaleKey> locales, String fieldName, String initLocale) {
        if (locales == null) {
            return Promise.pure(null)
        }
        CountryLocaleKey localeKey = locales.get(initLocale)

        if (localeKey != null) {
            Object obj = getAndCacheField(CountryLocaleKey.class, fieldName).get(localeKey)
            if (obj != null) {
                Map<String, Object> map = new HashMap<>()
                map.put(initLocale, obj)
                return Promise.pure(map)
            }
        }

        return localeService.get(new LocaleId(initLocale)).then { com.junbo.identity.spec.v1.model.Locale locale1 ->
            if (locale1 == null || locale1.fallbackLocale == null) {
                return Promise.pure(null)
            }

            return fillCountryLocaleKey(locales, fieldName, locale1.fallbackLocale.toString())
        }
    }

    private Field getAndCacheField(Class cls, String fieldName) {
        String key = cls.toString() + ":" + fieldName
        if (fieldMap.get(key) == null) {
            Field field = cls.getDeclaredField(fieldName)
            field.setAccessible(true)
            fieldMap.put(key, field)
        }

        return fieldMap.get(key)
    }
}
