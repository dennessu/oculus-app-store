/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.CurrencyTypeDao;
import com.junbo.payment.db.entity.CurrencyTypeEntity;
import com.junbo.payment.db.mapper.Currency;
import com.junbo.payment.db.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * currency Repository.
 */
public class CurrencyTypeRepository extends DomainDataRepository<CurrencyTypeEntity, CurrencyTypeDao> {
    @Override
    public void setDao(CurrencyTypeDao dao) {
        this.dao = dao;
    }

    @Autowired
    private PaymentMapper paymentMapperImpl;

    public Currency getCurrencyByCode(String currencyCode){
        for(CurrencyTypeEntity entity : getDomainData()){
            if(entity.getCurrencyCode().equals(currencyCode)){
                return paymentMapperImpl.toCurrency(entity, new MappingContext());
            }
        }
        return null;
    }
}
