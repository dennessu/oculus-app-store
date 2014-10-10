/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.IPredicate;
import com.junbo.payment.db.dao.payment.MerchantAccountDao;
import com.junbo.payment.db.entity.payment.MerchantAccountEntity;
import java.util.List;

/**
 * merchant Repository.
 */
public class MerchantAccountRepository extends DomainDataRepository<MerchantAccountEntity, MerchantAccountDao> {
    @Override
    public void setDao(MerchantAccountDao dao) {
        this.dao = dao;
    }

    public String getMerchantAccountRef(final Integer paymentProviderId, final String currencyIsoNum){
        List<MerchantAccountEntity> entities = getDomainData();
        List<MerchantAccountEntity> results = CommonUtil.filter(entities, new IPredicate<MerchantAccountEntity>() {
            @Override
            public boolean apply(MerchantAccountEntity type) {
                return type.getCurrency().equalsIgnoreCase(currencyIsoNum);
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
                            return type.getPaymentProviderId() == paymentProviderId;
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
}
