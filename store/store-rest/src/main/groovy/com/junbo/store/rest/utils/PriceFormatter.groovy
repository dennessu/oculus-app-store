package com.junbo.store.rest.utils

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.store.clientproxy.ResourceContainer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 2/10/2015.
 */
@CompileStatic
@Component('storePriceFormatter')
class PriceFormatter {

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    String formatPrice(BigDecimal value, CurrencyId currencyId) {
        if (value == null || currencyId == null) {
            return null
        }

        Currency currency = resourceContainer.currencyResource.get(currencyId, new CurrencyGetOptions()).get()
        if (currency.symbolPosition.equalsIgnoreCase(Constants.CurrencySymbolPosition.AFTER)) {
            return value.setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_UP) + currency.symbol
        } else {
            return currency.symbol + value.setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_UP)
        }
    }

}
