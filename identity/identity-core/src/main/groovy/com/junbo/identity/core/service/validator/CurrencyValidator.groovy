package com.junbo.identity.core.service.validator

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface CurrencyValidator {
    Promise<Currency> validateForGet(CurrencyId currencyId)
    Promise<Void> validateForSearch(CurrencyListOptions options)
    Promise<Void> validateForCreate(Currency currency)
    Promise<Void> validateForUpdate(CurrencyId currencyId, Currency currency, Currency oldCurrency)
}