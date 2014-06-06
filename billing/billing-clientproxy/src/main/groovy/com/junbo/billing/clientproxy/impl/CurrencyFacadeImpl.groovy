package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.CurrencyFacade
import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.promise.Promise

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-4.
 */
class CurrencyFacadeImpl implements CurrencyFacade {

    @Resource(name = 'billingCurrencyClient')
    private CurrencyResource currencyResource

    @Override
    Promise<Currency> getCurrency(String currencyId) {
        return currencyResource.get(new CurrencyId(currencyId), new CurrencyGetOptions())
    }
}
