package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.core.service.validator.CurrencyValidator
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class CurrencyValidatorImpl implements CurrencyValidator {
    private List<String> validCurrencies

    private String defaultCurrency

    @Override
    boolean isValid(String currency) {
        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        return validCurrencies.contains(currency)
    }

    @Override
    String getDefault() {
        return defaultCurrency
    }

    @Required
    void setValidCurrencies(List<String> validCurrencies) {
        this.validCurrencies = validCurrencies
    }

    @Required
    void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> validateForGet(CurrencyId countryId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(CurrencyListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(com.junbo.identity.spec.v1.model.Currency country) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(CurrencyId currencyId, com.junbo.identity.spec.v1.model.Currency currency, com.junbo.identity.spec.v1.model.Currency oldCurrency) {
        return null
    }
}
