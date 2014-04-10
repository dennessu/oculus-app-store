package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.CurrencyValidator
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
}
