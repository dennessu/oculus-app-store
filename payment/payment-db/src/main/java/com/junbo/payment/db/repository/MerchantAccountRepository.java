/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.IPredicate;
import com.junbo.payment.db.dao.payment.MerchantAccountDao;
import com.junbo.payment.db.entity.payment.MerchantAccountEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.internal.MerchantAccount;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * merchant Repository.
 */
public class MerchantAccountRepository extends DomainDataRepository<MerchantAccountEntity, MerchantAccountDao> {
    private PaymentMapper paymentMapper;
    @Override
    public void setDao(MerchantAccountDao dao) {
        this.dao = dao;
    }

    public String getMerchantAccountRef(final Integer paymentProviderId, final String currencyIsoNum){
        List<MerchantAccountEntity> entities = getDomainData();
        List<MerchantAccountEntity> results = CommonUtil.filter(entities, new IPredicate<MerchantAccountEntity>() {
            @Override
            public boolean apply(MerchantAccountEntity type) {
                return type.getPaymentProviderId() == paymentProviderId;
            }
        });
        if(results.size() == 1){
            return results.get(0).getMerchantAccountRef();
        }else if(results.size() > 1){
            //continue try to filter
            List<MerchantAccountEntity> providerResults = CommonUtil.filter(results,
                    new IPredicate<MerchantAccountEntity>() {
                        @Override
                        public boolean apply(MerchantAccountEntity type) {
                            return type.getCurrency().equalsIgnoreCase(currencyIsoNum) ||
                                    CommonUtil.isNullOrEmpty(type.getCurrency());
                        }
                    });
            if(providerResults.isEmpty()){
                return results.get(0).getMerchantAccountRef();
            }else{
                return providerResults.get(0).getMerchantAccountRef();
            }
        }
        return null;
    }

    public String getMerchantAccountRef(Integer merchantId){
        List<MerchantAccountEntity> entities = getDomainData();
        for(MerchantAccountEntity entity : entities){
            if(entity.getMerchantAccountId().equals(merchantId)){
                return entity.getMerchantAccountRef();
            }
        }
        return null;
    }

    public Integer getMerchantAccountIdByRef(String merchantRef){
        if(CommonUtil.isNullOrEmpty(merchantRef)){
            return null;
        }
        List<MerchantAccountEntity> entities = getDomainData();
        for(MerchantAccountEntity entity : entities){
            if(entity.getMerchantAccountRef().equalsIgnoreCase(merchantRef)){
                return entity.getMerchantAccountId();
            }
        }
        return null;
    }

    @Transactional
    public MerchantAccount get(Integer id){
        MerchantAccountEntity entity = this.dao.get(id);
        if (entity != null) {
            return paymentMapper.toMerchantAccount(entity, new MappingContext());
        }

        return null;
    }

    @Transactional
    public MerchantAccount save(MerchantAccount provider){
        if(provider.getCreatedTime() == null){
            provider.setCreatedTime(new Date());
        }
        MerchantAccountEntity entity = paymentMapper.toMerchantAccountEntity(provider, new MappingContext());
        Integer savedId = this.dao.save(entity);
        return get(savedId);
    }

    @Transactional
    public MerchantAccount update(MerchantAccount model, MerchantAccount oldModel){
        if(model.getCreatedTime() == null){
            model.setCreatedTime(new Date());
        }
        MerchantAccountEntity entity = paymentMapper.toMerchantAccountEntity(model, new MappingContext());
        MerchantAccountEntity updated = this.dao.update(entity);
        return get(updated.getMerchantAccountId());
    }

    @Transactional
    void deleteClient(MerchantAccount model){
        MerchantAccountEntity entity = paymentMapper.toMerchantAccountEntity(model, new MappingContext());
        this.dao.delete(entity);
    }

    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

}
