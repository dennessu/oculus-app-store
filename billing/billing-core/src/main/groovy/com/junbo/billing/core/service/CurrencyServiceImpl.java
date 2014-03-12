/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.db.repository.CurrencyRepository;
import com.junbo.billing.spec.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
@Transactional
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public Currency getCurrencyByName(String name) {
        return currencyRepository.getCurrency(name);
    }

    @Override
    public List<Currency> getCurrencies() {
        return currencyRepository.getCurrencies();
    }

    @Override
    public Boolean exists(String name) {
        Currency currency = getCurrencyByName(name);
        return currency != null;
    }
}
