/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.common.id.PIType;
import com.junbo.payment.db.dao.payment.PaymentProviderDao;
import com.junbo.payment.db.entity.payment.PaymentProviderEntity;

import java.util.List;

/**
 * payment provider Repository.
 */
public class PaymentProviderRepository  extends DomainDataRepository<PaymentProviderEntity, PaymentProviderDao> {
    @Override
    public void setDao(PaymentProviderDao dao) {
        this.dao = dao;
    }

    public String getProviderName(PIType piType){
        List<PaymentProviderEntity> entities = getDomainData();
        for(PaymentProviderEntity entity : entities){
            if(entity.getPiType().equals(piType.getId())){
                return entity.getProviderName();
            }
        }
        return null;
    }

    public String getProviderName(Integer providerId){
        List<PaymentProviderEntity> entities = getDomainData();
        for(PaymentProviderEntity entity : entities){
            if(entity.getId().equals(providerId)){
                return entity.getProviderName();
            }
        }
        return null;
    }

    public Integer getProviderId(String providerName){
        List<PaymentProviderEntity> entities = getDomainData();
        for(PaymentProviderEntity entity : entities){
            if(entity.getProviderName().equals(providerName)){
                return entity.getId();
            }
        }
        return null;
    }
}
