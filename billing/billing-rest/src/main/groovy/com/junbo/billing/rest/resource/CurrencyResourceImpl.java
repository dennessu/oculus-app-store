/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.CurrencyService;
import com.junbo.billing.spec.model.Currency;
import com.junbo.billing.spec.resource.CurrencyResource;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 * Created by xmchen on 14-2-13.
 */
@Scope("prototype")
public class CurrencyResourceImpl implements CurrencyResource {

    @Autowired
    private CurrencyService currencyService;

    @Override
    public Promise<Results<Currency>> getCurrencies() {
        Results<Currency> result = new Results<>();
        result.setItems(currencyService.getCurrencies());

        return Promise.pure(result);
    }

    @Override
    public Promise<Currency> getCurrency(String name) {
        return Promise.pure(currencyService.getCurrencyByName(name));
    }
}
