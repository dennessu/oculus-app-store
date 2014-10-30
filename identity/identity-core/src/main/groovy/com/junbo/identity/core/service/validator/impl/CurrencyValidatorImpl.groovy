package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.CurrencyValidator
import com.junbo.identity.data.identifiable.SymbolPosition
import com.junbo.identity.service.CurrencyService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.model.CurrencyLocaleKey
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Check localeKey's key list
 * Check minimum and maximum localeKey's value's length
 * Check minimum and maximum symbol length
 * Check minimum and maximum numberAfterDecimal length
 * Check minimum and maximum minAuthAmount length
 * Check currencyCode duplicate
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class CurrencyValidatorImpl implements CurrencyValidator {

    private CurrencyService currencyService

    private Integer minSymbolLength
    private Integer maxSymbolLength

    private Integer minLocaleKeyShortNameLength
    private Integer maxLocaleKeyShortNameLength

    private Integer minLocaleKeyLongNameLength
    private Integer maxLocaleKeyLongNameLength

    private Integer minNumberAfterDecimalLength
    private Integer maxNumberAfterDecimalLength

    private Integer minMinAuthAmount
    private Integer maxMinAuthAmount

    @Required
    void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService
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
    void setMinNumberAfterDecimalLength(Integer minNumberAfterDecimalLength) {
        this.minNumberAfterDecimalLength = minNumberAfterDecimalLength
    }

    @Required
    void setMaxNumberAfterDecimalLength(Integer maxNumberAfterDecimalLength) {
        this.maxNumberAfterDecimalLength = maxNumberAfterDecimalLength
    }

    @Required
    void setMinLocaleKeyShortNameLength(Integer minLocaleKeyShortNameLength) {
        this.minLocaleKeyShortNameLength = minLocaleKeyShortNameLength
    }

    @Required
    void setMaxLocaleKeyShortNameLength(Integer maxLocaleKeyShortNameLength) {
        this.maxLocaleKeyShortNameLength = maxLocaleKeyShortNameLength
    }

    @Required
    void setMinLocaleKeyLongNameLength(Integer minLocaleKeyLongNameLength) {
        this.minLocaleKeyLongNameLength = minLocaleKeyLongNameLength
    }

    @Required
    void setMaxLocaleKeyLongNameLength(Integer maxLocaleKeyLongNameLength) {
        this.maxLocaleKeyLongNameLength = maxLocaleKeyLongNameLength
    }

    @Required
    void setMinMinAuthAmount(Integer minMinAuthAmount) {
        this.minMinAuthAmount = minMinAuthAmount
    }

    @Required
    void setMaxMinAuthAmount(Integer maxMinAuthAmount) {
        this.maxMinAuthAmount = maxMinAuthAmount
    }

    @Override
    Promise<Currency> validateForGet(CurrencyId currencyId) {
        if (currencyId == null || currencyId.value == null) {
            throw new IllegalArgumentException('currencyId is null')
        }

        return currencyService.get(currencyId).then { Currency currency ->
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
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return currencyService.get(new CurrencyId(currency.currencyCode)).then { Currency existing ->
            if (existing != null) {
                throw AppCommonErrors.INSTANCE.fieldDuplicate('currencyCode').exception()
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
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (currencyId != oldCurrency.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicCurrencyInfo(currency)

        if (currency.currencyCode != oldCurrency.currencyCode) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('currencyCode').exception()
        }

        return Promise.pure(null)
    }

    private void checkBasicCurrencyInfo(Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException('currency is null')
        }

        if (currency.currencyCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('currencyCode').exception()
        }

        if (currency.symbol == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('symbol').exception()
        }
        if (currency.symbol.length() > maxSymbolLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('symbol', maxSymbolLength).exception()
        }
        if (currency.symbol.length() < minSymbolLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('symbol', minSymbolLength).exception()
        }

        if (currency.symbolPosition == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('symbolPosition').exception()
        }
        if (!SymbolPosition.values().any { SymbolPosition position ->
            return currency.symbolPosition == position.toString()
        }) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('symbolPosition', SymbolPosition.values().join(',')).exception()
        }

        if (currency.numberAfterDecimal == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('numberAfterDecimal').exception()
        }
        if (currency.numberAfterDecimal > maxNumberAfterDecimalLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('numberAfterDecimal', maxNumberAfterDecimalLength).exception()
        }
        if (currency.numberAfterDecimal < minNumberAfterDecimalLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('numberAfterDecimal', minNumberAfterDecimalLength).exception()
        }

        if (currency.minAuthAmount == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('minAuthAmount').exception()
        }
        if (currency.minAuthAmount < minMinAuthAmount) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('minAuthAmount',
                    'minAuthAmount invalid, must bigger than ' + minMinAuthAmount).exception()
        }
        if (currency.minAuthAmount > maxMinAuthAmount) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('minAuthAmount',
                    'minAuthAmount invalid, must less than ' + maxMinAuthAmount).exception()
        }

        if (currency.locales == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locales').exception()
        }

        currency.locales.each { Map.Entry<String, CurrencyLocaleKey> entry ->
            if (StringUtils.isEmpty(entry.key)) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.key').exception()
            }

            CurrencyLocaleKey value = entry.value
            if (value == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value').exception()
            }
            if (value.shortName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value.shortName').exception()
            }
            if (value.shortName.length() > maxLocaleKeyShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('locales.value.shortName', maxLocaleKeyShortNameLength).exception()
            }
            if (value.shortName.length() < minLocaleKeyShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('locales.value.shortName', minLocaleKeyShortNameLength).exception()
            }

            if (value.longName == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.value.longName').exception()
            }
            if (value.longName.length() > maxLocaleKeyLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('locales.value.longName', maxLocaleKeyLongNameLength).exception()
            }
            if (value.longName.length() < minLocaleKeyLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('locales.value.longName', minLocaleKeyLongNameLength).exception()
            }
        }

        if (!ValidatorUtil.isValidCurrencyCode(currency.currencyCode)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('currencyCode').exception()
        }
    }
}
