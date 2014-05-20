package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.data.identifiable.NegativeFormat
import com.junbo.identity.data.identifiable.SymbolPosition
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.core.service.validator.CurrencyValidator
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class CurrencyValidatorImpl implements CurrencyValidator {

    private CurrencyRepository currencyRepository

    private List<String> allowedLocaleKeys

    private Integer minSymbolLength
    private Integer maxSymbolLength

    private Integer minDecimalSymbolLength
    private Integer maxDecimalSymbolLength

    private Integer minNumberAfterDecimalLength
    private Integer maxNumberAfterDecimalLength

    private Integer minDigitGroupingSymbolLength
    private Integer maxDigitGroupingSymbolLength

    private Integer minLocaleKeyValueLength
    private Integer maxLocaleKeyValueLength

    @Required
    void setCurrencyRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository
    }

    @Required
    void setAllowedLocaleKeys(List<String> allowedLocaleKeys) {
        this.allowedLocaleKeys = allowedLocaleKeys
    }

    @Required
    void setMinSymbolLength(Integer minSymbolLength) {
        this.minSymbolLength = minSymbolLength
    }

    @Required
    void setMaxSymbolLength(Integer maxSymbolLength) {
        this.maxSymbolLength = maxSymbolLength
    }

    @Required
    void setMinDecimalSymbolLength(Integer minDecimalSymbolLength) {
        this.minDecimalSymbolLength = minDecimalSymbolLength
    }

    @Required
    void setMaxDecimalSymbolLength(Integer maxDecimalSymbolLength) {
        this.maxDecimalSymbolLength = maxDecimalSymbolLength
    }

    @Required
    void setMinNumberAfterDecimalLength(Integer minNumberAfterDecimalLength) {
        this.minNumberAfterDecimalLength = minNumberAfterDecimalLength
    }

    @Required
    void setMaxNumberAfterDecimalLength(Integer maxNumberAfterDecimalLength) {
        this.maxNumberAfterDecimalLength = maxNumberAfterDecimalLength
    }

    @Required
    void setMinDigitGroupingSymbolLength(Integer minDigitGroupingSymbolLength) {
        this.minDigitGroupingSymbolLength = minDigitGroupingSymbolLength
    }

    @Required
    void setMaxDigitGroupingSymbolLength(Integer maxDigitGroupingSymbolLength) {
        this.maxDigitGroupingSymbolLength = maxDigitGroupingSymbolLength
    }

    @Required
    void setMinLocaleKeyValueLength(Integer minLocaleKeyValueLength) {
        this.minLocaleKeyValueLength = minLocaleKeyValueLength
    }

    @Required
    void setMaxLocaleKeyValueLength(Integer maxLocaleKeyValueLength) {
        this.maxLocaleKeyValueLength = maxLocaleKeyValueLength
    }

    @Override
    Promise<Currency> validateForGet(CurrencyId currencyId) {
        if (currencyId == null || currencyId.value == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        return currencyRepository.get(currencyId).then { Currency currency ->
            if (currency == null) {
                throw AppErrors.INSTANCE.currencyNotFound(currencyId).exception()
            }

            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Void> validateForSearch(CurrencyListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Currency currency) {
        checkBasicCurrencyInfo(currency)

        if (currency.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return currencyRepository.get(new CurrencyId(currency.currencyCode)).then { Currency existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('currencyCode').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(CurrencyId currencyId, Currency currency, Currency oldCurrency) {
        if (currencyId == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        if (currencyId != currency.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (currencyId != oldCurrency.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicCurrencyInfo(currency)

        if (currency.currencyCode != oldCurrency.currencyCode) {
            throw AppErrors.INSTANCE.fieldInvalid('currencyCode').exception()
        }

        return Promise.pure(null)
    }

    private void checkBasicCurrencyInfo(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        if (currency.currencyCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('currencyCode').exception()
        }

        if (currency.symbol == null) {
            throw AppErrors.INSTANCE.fieldRequired('symbol').exception()
        }
        if (currency.symbol.length() > maxSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooLong('symbol', maxSymbolLength).exception()
        }
        if (currency.symbol.length() < minSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooShort('symbol', minSymbolLength).exception()
        }

        if (currency.symbolPosition == null) {
            throw AppErrors.INSTANCE.fieldRequired('symbolPosition').exception()
        }
        if (!SymbolPosition.values().any { SymbolPosition position ->
            return currency.symbolPosition == position.toString()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('symbolPosition', SymbolPosition.values().join(',')).exception()
        }

        if (currency.decimalSymbol == null) {
            throw AppErrors.INSTANCE.fieldRequired('decimalSymbol').exception()
        }
        if (currency.decimalSymbol.length() > maxDecimalSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooLong('decimalSymbol', maxDecimalSymbolLength).exception()
        }
        if (currency.decimalSymbol.length() < minDecimalSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooShort('decimalSymbol', minDecimalSymbolLength).exception()
        }

        if (currency.numberAfterDecimal == null) {
            throw AppErrors.INSTANCE.fieldRequired('numberAfterDecimal').exception()
        }
        if (currency.numberAfterDecimal > maxNumberAfterDecimalLength) {
            throw AppErrors.INSTANCE.fieldTooLong('numberAfterDecimal', maxNumberAfterDecimalLength).exception()
        }
        if (currency.numberAfterDecimal < minNumberAfterDecimalLength) {
            throw AppErrors.INSTANCE.fieldTooShort('numberAfterDecimal', minNumberAfterDecimalLength).exception()
        }

        if (currency.negativeFormat == null) {
            throw AppErrors.INSTANCE.fieldRequired('negativeFormat').exception()
        }
        if (!NegativeFormat.values().any { NegativeFormat negativeFormat ->
            return negativeFormat.toString() == currency.negativeFormat
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('negativeFormat', NegativeFormat.values().join(',')).exception()
        }

        if (currency.digitGroupingSymbol == null) {
            throw AppErrors.INSTANCE.fieldRequired('digitGroupingSymbol').exception()
        }
        if (currency.digitGroupingSymbol.length() < minDigitGroupingSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooShort('digitGroupingSymbol', minDigitGroupingSymbolLength).exception()
        }
        if (currency.digitGroupingSymbol.length() > maxDigitGroupingSymbolLength) {
            throw AppErrors.INSTANCE.fieldTooLong('digitGroupingSymbol', maxDigitGroupingSymbolLength).exception()
        }

        if (currency.digitGroupingLength == null) {
            throw AppErrors.INSTANCE.fieldRequired('digitGroupingLength').exception()
        }

        if (currency.localeKeys == null) {
            throw AppErrors.INSTANCE.fieldRequired('localeKeys').exception()
        }
        currency.localeKeys.each { Map.Entry<String, String> entry ->
            String key = entry.key
            if (!(key in allowedLocaleKeys)) {
                throw AppErrors.INSTANCE.fieldInvalid('localeKeys.key', allowedLocaleKeys.join(',')).exception()
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

        if (!ValidatorUtil.isValidCurrencyCode(currency.currencyCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('currencyCode').exception()
        }
    }
}
