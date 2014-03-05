/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.CountryTypeDao;
import com.junbo.payment.db.entity.CountryTypeEntity;
import com.junbo.payment.db.mapper.Country;
import com.junbo.payment.db.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * country Repository.
 */
public class CountryTypeRepository extends DomainDataRepository<CountryTypeEntity, CountryTypeDao> {
    @Override
    public void setDao(CountryTypeDao dao) {
        this.dao = dao;
    }

    @Autowired
    private PaymentMapper paymentMapperImpl;

    public Country getCountryBy2Code(String countryCode){
        for(CountryTypeEntity entity : getDomainData()){
            if(entity.getCountryCode().equals(countryCode)){
                return paymentMapperImpl.toCountry(entity, new MappingContext());
            }
        }
        return null;
    }

    public Country getCountryBy3Code(String country3Code){
        for(CountryTypeEntity entity : getDomainData()){
            if(entity.getCountry3Code().equals(country3Code)){
                return paymentMapperImpl.toCountry(entity, new MappingContext());
            }
        }
        return null;
    }
}
