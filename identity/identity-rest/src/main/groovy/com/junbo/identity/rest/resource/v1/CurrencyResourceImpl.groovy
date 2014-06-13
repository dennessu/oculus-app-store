package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.CurrencyFilter
import com.junbo.identity.core.service.validator.CurrencyValidator
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.model.CurrencyLocaleKey
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class CurrencyResourceImpl implements CurrencyResource {
    private static final String IDENTITY_ADMIN_SCOPE = 'identity.admin'

    private Map<String, Field> hashMap = new ConcurrentHashMap<String, Field>()

    @Autowired
    private CurrencyRepository currencyRepository

    @Autowired
    private LocaleRepository localeRepository

    @Autowired
    private CurrencyFilter currencyFilter

    @Autowired
    private CurrencyValidator currencyValidator

    @Override
    Promise<Currency> create(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException('country is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        currency = currencyFilter.filterForCreate(currency)

        return currencyValidator.validateForCreate(currency).then {
            return currencyRepository.create(currency).then { Currency newCurrency ->
                Created201Marker.mark(newCurrency.id)
                newCurrency = currencyFilter.filterForGet(newCurrency, null)

                return Promise.pure(newCurrency)
            }
        }
    }

    @Override
    Promise<Currency> put(CurrencyId currencyId, Currency currency) {
        if (currencyId == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }

        return currencyRepository.get(currencyId).then { Currency oldCurrency ->
            if (oldCurrency == null) {
                throw AppErrors.INSTANCE.currencyNotFound(currencyId).exception()
            }

            currency = currencyFilter.filterForPut(currency, oldCurrency)

            return currencyValidator.validateForUpdate(currencyId, currency, oldCurrency).then {
                return currencyRepository.update(currency).then { Currency newCurrency ->
                    newCurrency = currencyFilter.filterForGet(newCurrency, null)
                    return Promise.pure(newCurrency)
                }
            }
        }
    }

    @Override
    Promise<Currency> patch(CurrencyId currencyId, Currency currency) {
        if (currencyId == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }


        return currencyRepository.get(currencyId).then { Currency oldCurrency ->
            if (oldCurrency == null) {
                throw AppErrors.INSTANCE.currencyNotFound(currencyId).exception()
            }

            currency = currencyFilter.filterForPatch(currency, oldCurrency)

            return currencyValidator.validateForUpdate(
                    currencyId, currency, oldCurrency).then {
                return currencyRepository.update(currency).then { Currency newCurrency ->
                    newCurrency = currencyFilter.filterForGet(newCurrency, null)
                    return Promise.pure(newCurrency)
                }
            }
        }
    }

    @Override
    Promise<Currency> get(CurrencyId currencyId, CurrencyGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return currencyValidator.validateForGet(currencyId).then {
            return currencyRepository.get(currencyId).then { Currency newCurrency ->
                if (newCurrency == null) {
                    throw AppErrors.INSTANCE.currencyNotFound(currencyId).exception()
                }
                return filterCurrency(newCurrency, getOptions).then { Currency filterCurrency ->
                    filterCurrency = currencyFilter.filterForGet(filterCurrency, null)
                    return Promise.pure(filterCurrency)
                }
            }
        }
    }

    @Override
    Promise<Results<Currency>> list(CurrencyListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return currencyValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { List<Currency> currencyList ->
                def result = new Results<Currency>(items: [])

                currencyList.each { Currency newCurrency ->
                    newCurrency = currencyFilter.filterForGet(newCurrency, null)

                    if (newCurrency != null) {
                        result.items.add(newCurrency)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Void> delete(CurrencyId currencyId) {
        if (currencyId == null) {
            throw new IllegalArgumentException('currencyId is null')
        }
        return currencyValidator.validateForGet(currencyId).then {
            return currencyRepository.delete(currencyId)
        }
    }

    private Promise<List<Currency>> search(CurrencyListOptions listOptions) {
        return currencyRepository.searchAll(listOptions.limit, listOptions.offset)
    }

    private Promise<Currency> filterCurrency(Currency currency, CurrencyGetOptions getOptions) {
        if (StringUtils.isEmpty(getOptions.locale)) {
            return Promise.pure(currency)
        }

        return fillCurrencyLocaleKey(currency, getOptions.locale).then { Map<String, CurrencyLocaleKey> map ->
            currency.locales = map
            return Promise.pure(currency)
        }
    }

    Promise<Map<String, CurrencyLocaleKey>> fillCurrencyLocaleKey(Currency currency, String locale) {
        CurrencyLocaleKey currencyLocaleKey = new CurrencyLocaleKey()

        Field[] fields = CurrencyLocaleKey.class.getDeclaredFields()
        return Promise.each(Arrays.asList(fields)) { Field field ->
            field = getAndCacheField(CurrencyLocaleKey.class, field.getName())
            return fillCurrencyLocaleKey(currency, field.getName(), locale).then { Map<String, Object> value ->
                if (value == null || value.isEmpty()) {
                    return Promise.pure(null)
                }

                value.each { Map.Entry<String, Object> entry ->
                    field.set(currencyLocaleKey, entry.value)
                }

                return Promise.pure(null)
            }
        }.then {
            Map<String, CurrencyLocaleKey> map = new HashMap<>()
            map.put(locale, currencyLocaleKey)
            return Promise.pure(map)
        }
    }

    // return locale:fieldValue
    // if we can't find it in the end, will return error;
    // else will return the first locale with fieldValue
    Promise<Map<String, Object>> fillCurrencyLocaleKey(Currency currency, String fieldName, String initLocale) {
        CurrencyLocaleKey localeKey = currency.locales.get(initLocale)

        if (localeKey != null) {
            Object obj = getAndCacheField(CurrencyLocaleKey.class, fieldName).get(localeKey)
            if (obj != null) {
                Map<String, Object> map = new HashMap<>()
                map.put(initLocale, obj)
                return Promise.pure(map)
            }
        }

        return localeRepository.get(new LocaleId(initLocale)).then { com.junbo.identity.spec.v1.model.Locale locale1 ->
            if (locale1 == null || locale1.fallbackLocale == null) {
                return Promise.pure(null)
            }

            return fillCurrencyLocaleKey(currency, fieldName, locale1.fallbackLocale.toString())
        }
    }

    private Field getAndCacheField(Class cls, String fieldName) {
        String key = cls.toString() + ":" + fieldName
        if (hashMap.get(key) == null) {
            Field field = CurrencyLocaleKey.getDeclaredField(fieldName)
            field.setAccessible(true)
            hashMap.put(key, field)
        }

        return hashMap.get(key)
    }
}
