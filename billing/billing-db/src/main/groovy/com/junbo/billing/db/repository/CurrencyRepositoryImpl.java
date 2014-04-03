/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.entity.CurrencyEntity;
import com.junbo.billing.db.dao.CurrencyEntityDao;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.spec.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmchen on 14-2-19.
 */
public class CurrencyRepositoryImpl implements CurrencyRepository {

    @Autowired
    private CurrencyEntityDao currencyEntityDao;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Currency getCurrency(String name) {
        CurrencyEntity entity = currencyEntityDao.get(name);
        if(entity != null) {
            return modelMapper.toCurrency(entity, new MappingContext());
        }
        else {
            return null;
        }
    }

    @Override
    public List<Currency> getCurrencies() {

        List<CurrencyEntity> entities = currencyEntityDao.loadAll();
        List<Currency> currencies = new ArrayList<Currency>();
        for(CurrencyEntity entity : entities) {
            Currency currency = modelMapper.toCurrency(entity, new MappingContext());
            currencies.add(currency);
        }
        return currencies;
    }
}
