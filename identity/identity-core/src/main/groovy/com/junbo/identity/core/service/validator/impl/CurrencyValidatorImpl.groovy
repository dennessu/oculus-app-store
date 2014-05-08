package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.common.util.ValidatorUtil
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

    @Required
    void setCurrencyRepository(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository
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
        if (currency.locales == null) {
            throw AppErrors.INSTANCE.fieldRequired('locales').exception()
        }
        if (currency.futureExpansion == null) {
            throw AppErrors.INSTANCE.fieldRequired('futureExpansion').exception()
        }
        if (!ValidatorUtil.isValidCurrencyCode(currency.currencyCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('currencyCode').exception()
        }
    }
}
