/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.IPredicate;
import com.junbo.payment.db.dao.payment.PaymentProviderDao;
import com.junbo.payment.db.entity.payment.PaymentProviderEntity;
import com.junbo.payment.spec.internal.ProviderCriteria;

import java.util.List;

/**
 * payment provider Repository.
 */
public class PaymentProviderRepository  extends DomainDataRepository<PaymentProviderEntity, PaymentProviderDao> {
    @Override
    public void setDao(PaymentProviderDao dao) {
        this.dao = dao;
    }

    public String getProviderName(final ProviderCriteria criteria){
        if(criteria.getPiType() == null){
            return null;
        }
        List<PaymentProviderEntity> entities = getDomainData();
        List<PaymentProviderEntity> results = CommonUtil.filter(entities, new IPredicate<PaymentProviderEntity>() {
            @Override
            public boolean apply(PaymentProviderEntity type) {
                return type.getPiType().equals(criteria.getPiType().getId());
            }
        });
        if(results.size() == 1){
            return results.get(0).getProviderName();
        }else if(results.size() > 1){
            //continue filter:
            List<PaymentProviderEntity> filterResults = CommonUtil.filter(entities, new IPredicate<PaymentProviderEntity>() {
                @Override
                public boolean apply(PaymentProviderEntity entity) {
                    boolean filterResult = true;
                    if(!CommonUtil.isNullOrEmpty(criteria.getCountry())){
                        filterResult = filterResult && entity.getCountryCode().equalsIgnoreCase(criteria.getCountry());
                    }
                    if(!CommonUtil.isNullOrEmpty(criteria.getCurrency())){
                        filterResult = filterResult && entity.getCurrency().equalsIgnoreCase(criteria.getCurrency());
                    }
                    return filterResult;
                }
            });
            if(filterResults.isEmpty()){
                return results.get(0).getProviderName();
            }else{
                return filterResults.get(0).getProviderName();
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
