/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.CurrencyService;
import com.junbo.billing.spec.model.Currency;
import com.junbo.billing.spec.resource.CurrencyResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
@Scope("prototype")
public class CurrencyResourceImpl implements CurrencyResource {

    @Autowired
    private CurrencyService currencyService;

    @Override
    public Promise<List<Currency>> getCurrencies() {
        return Promise.pure(currencyService.getCurrencies());
    }

    @Override
    public Promise<Currency> getCurrency(String name) {
        return Promise.pure(currencyService.getCurrencyByName(name));
    }
}
