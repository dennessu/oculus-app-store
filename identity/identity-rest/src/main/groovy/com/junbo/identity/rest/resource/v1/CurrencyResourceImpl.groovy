package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
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

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam

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

    @Override
    Promise<Currency> create(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException('country is null')
        }

        currencyRepository.create(currency).then { Currency newCurrency ->
            created201Marker.mark(newCurrency.id)
            return Promise.pure(newCurrency)
        }
    }

    @Override
    Promise<Currency> put(@PathParam("currencyId") CurrencyId currencyId, Currency currency) {
        if (currencyId == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        return currencyRepository.get(currencyId).then { Currency oldCurrency ->
            if (oldCurrency == null) {
                throw AppErrors.INSTANCE.CurrencyNotFound(currencyId).exception()
            }
            currencyRepository.update(currency).then { Currency newCurrency ->
                return Promise.pure(newCurrency)
            }
        }
    }

    @Override
    Promise<Currency> patch(@PathParam("currencyId") CurrencyId currencyId, Currency currency) {
        return null
    }

    @Override
    Promise<Currency> get(@PathParam("currencyId") CurrencyId currencyId, @BeanParam CurrencyGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return currencyRepository.get(currencyId)
    }

    @Override
    Promise<Results<Currency>> list(@BeanParam CurrencyListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        currencyRepository.search(listOptions).then { List<Currency> currencyList ->
            def result = new Results<Currency>(items: [])

            currencyList.each { Currency newCurrency ->
                result.items.add(newCurrency)
            }

            return Promise.pure(result)
        }
    }

    @Override
    Promise<Void> delete(@PathParam("currencyId") CurrencyId currencyId) {
        return currencyRepository.delete(currencyId)
    }
}
