package com.junbo.order.mock

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response

/**
 * Created by xmchen on 14-6-4.
 */
@CompileStatic
@Component('mockCurrencyResource')
class MockCurrencyResource extends BaseMock implements CurrencyResource {
    @Override
    Promise<Currency> create(Currency currency) {
        return null
    }

    @Override
    Promise<Currency> put(CurrencyId currencyId, Currency currency) {
        return null
    }

    @Override
    Promise<Currency> patch(CurrencyId currencyId, Currency currency) {
        return null
    }

    @Override
    Promise<Currency> get(CurrencyId currencyId,CurrencyGetOptions getOptions) {
        Currency currency = new Currency()
        currency.setId(currencyId)
        currency.setCurrencyCode('USD')
        return Promise.pure(currency)
    }

    @Override
    Promise<Results<Currency>> list(CurrencyListOptions listOptions) {
        return null
    }

    @Override
    Promise<Response> delete(CurrencyId currencyId) {
        return null
    }
}
