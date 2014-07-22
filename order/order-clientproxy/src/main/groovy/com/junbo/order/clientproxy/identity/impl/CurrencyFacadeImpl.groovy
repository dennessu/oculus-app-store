package com.junbo.order.clientproxy.identity.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.CurrencyFacade
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Implementation of currency facade.
 */
@Component('orderCurrencyFacade')
@CompileStatic
@TypeChecked
class CurrencyFacadeImpl implements CurrencyFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyFacadeImpl)

    @Resource(name = 'order.identityCurrencyClient')
    CurrencyResource currencyResource

    @Override
    Promise<Currency> getCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw AppCommonErrors.INSTANCE.parameterRequired('currency').exception()
        }
            return currencyResource.get(new CurrencyId(currency), new CurrencyGetOptions()).recover {
            Throwable throwable ->
                LOGGER.error('name=error_in_get_currency: ' + currency, throwable)
                throw AppErrors.INSTANCE.currencyNotValid(currency).exception()
        }.then { com.junbo.identity.spec.v1.model.Currency c ->
            if(c == null) {
                LOGGER.error('name=currency_is_null: ' + currency)
                throw AppErrors.INSTANCE.currencyNotValid(currency).exception()
            }
            return Promise.pure(c)
        }
    }
}
