/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.mock;

import com.junbo.billing.clientproxy.CurrencyFacade;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-6-4.
 */
public class MockCurrencyFacade implements CurrencyFacade {
    @Override
    public Promise<Currency> getCurrency(String currencyId) {
        Currency currency = new Currency();
        currency.setId(new CurrencyId("USD"));
        currency.setNumberAfterDecimal(2);
        return Promise.pure(currency);
    }
}
