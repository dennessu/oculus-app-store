/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.db.dao.PaymentInstrumentTypeDao;
import com.junbo.payment.db.entity.PaymentInstrumentTypeEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PI type Repository.
 */
public class PITypeRepository extends DomainDataRepository<PaymentInstrumentTypeEntity, PaymentInstrumentTypeDao> {
    @Override
    public void setDao(PaymentInstrumentTypeDao dao) {
        this.dao = dao;
    }

    @Autowired
    private PaymentMapper paymentMapperImpl;

    public PaymentInstrumentType getPITypeByName(String name){
        if(CommonUtil.isNullOrEmpty(name)){
            return null;
        }
        for(PaymentInstrumentTypeEntity entity : getDomainData()){
            if(entity.getName().equalsIgnoreCase(name)){
                return paymentMapperImpl.toPIType(entity, new MappingContext());
            }
        }
        return null;
    }
}
