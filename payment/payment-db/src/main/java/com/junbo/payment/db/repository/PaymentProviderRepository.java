/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.IPredicate;
import com.junbo.payment.db.dao.payment.PaymentProviderDao;
import com.junbo.payment.db.entity.payment.PaymentProviderEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.internal.PaymentProviderModel;
import com.junbo.payment.spec.internal.ProviderCriteria;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * payment provider Repository.
 */
public class PaymentProviderRepository  extends DomainDataRepository<PaymentProviderEntity, PaymentProviderDao> {
    private PaymentMapper paymentMapper;
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
            List<PaymentProviderEntity> filterResults = CommonUtil.filter(results, new IPredicate<PaymentProviderEntity>() {
                @Override
                public boolean apply(PaymentProviderEntity entity) {
                    boolean filterResult = true;
                    if(!CommonUtil.isNullOrEmpty(criteria.getCountry())){
                        filterResult = filterResult && entity.getCountryCode() != null &&
                                entity.getCountryCode().trim().equalsIgnoreCase(criteria.getCountry());
                    }
                    if(!CommonUtil.isNullOrEmpty(criteria.getCurrency())){
                        filterResult = filterResult && entity.getCurrency() != null &&
                                entity.getCurrency().equalsIgnoreCase(criteria.getCurrency());
                    }
                    return filterResult;
                }
            });
            if(filterResults.isEmpty()){
                return null;
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

    @Transactional
    public PaymentProviderModel get(Integer paymentProviderId){
        PaymentProviderEntity entity = this.dao.get(paymentProviderId);
        if (entity != null) {
            return paymentMapper.toProviderModel(entity, new MappingContext());
        }

        return null;
    }

    @Transactional
    public PaymentProviderModel save(PaymentProviderModel provider){
        if(provider.getCreatedTime() == null){
            provider.setCreatedTime(new Date());
        }
        PaymentProviderEntity entity = paymentMapper.toProviderEntity(provider, new MappingContext());
        Integer savedId = this.dao.save(entity);
        return get(savedId);
    }

    @Transactional
    public PaymentProviderModel update(PaymentProviderModel provider, PaymentProviderModel oldProvider){
        if(provider.getCreatedTime() == null){
            provider.setCreatedTime(new Date());
        }
        PaymentProviderEntity entity = paymentMapper.toProviderEntity(provider, new MappingContext());
        PaymentProviderEntity updated = this.dao.update(entity);
        return get(updated.getId());
    }

    @Transactional
    void deleteClient(PaymentProviderModel model){
        PaymentProviderEntity entity = paymentMapper.toProviderEntity(model, new MappingContext());
        this.dao.delete(entity);
    }

    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }
}
