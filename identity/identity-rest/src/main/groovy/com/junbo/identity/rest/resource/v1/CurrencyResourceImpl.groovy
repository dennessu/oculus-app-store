package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.CurrencyFilter
import com.junbo.identity.core.service.validator.CurrencyValidator
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class CurrencyResourceImpl implements CurrencyResource {
    @Autowired
    private CurrencyRepository currencyRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private CurrencyFilter currencyFilter

    @Autowired
    private CurrencyValidator currencyValidator

    @Override
    Promise<Currency> create(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException('country is null')
        }

        currency = currencyFilter.filterForCreate(currency)

        return currencyValidator.validateForCreate(currency).then {
            return currencyRepository.create(currency).then { Currency newCurrency ->
                created201Marker.mark(newCurrency.id)
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

                newCurrency = currencyFilter.filterForGet(newCurrency, null)
                return Promise.pure(newCurrency)
            }
        }
    }

    @Override
    Promise<Results<Currency>> list(CurrencyListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return currencyValidator.validateForSearch(listOptions).then {
            return currencyRepository.search(listOptions).then { List<Currency> currencyList ->
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
}
