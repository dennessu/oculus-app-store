/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

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

    public String getMerchantAccountRef(Integer paymentProviderId, String currencyIsoNum){
        if(paymentProviderId == null || currencyIsoNum == null){
            return null;
        }
        List<MerchantAccountEntity> entities = getDomainData();
        for(MerchantAccountEntity entity : entities){
            if(entity.getPaymentProviderId().equals(paymentProviderId) &&
                    entity.getCurrency().equalsIgnoreCase(currencyIsoNum)){
                return entity.getMerchantAccountRef();
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
        if(merchantRef == null || merchantRef.isEmpty()){
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
