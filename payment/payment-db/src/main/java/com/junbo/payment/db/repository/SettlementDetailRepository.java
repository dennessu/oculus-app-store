/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.payment.SettlementDetailDao;
import com.junbo.payment.db.entity.payment.SettlementDetailEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.internal.SettlementDetail;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-18.
 */
public class SettlementDetailRepository {
    private SettlementDetailDao settlementDetailDao;
    private PaymentMapper paymentMapper;
    @Required
    public void setSettlementDetailDao(SettlementDetailDao settlementDetailDao) {
        this.settlementDetailDao = settlementDetailDao;
    }
    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    public void saveDetails(List<SettlementDetail> details){
        if(details != null){
            for(SettlementDetail detail : details){
                settlementDetailDao.save(paymentMapper.toSettlementDetailEntity(detail, new MappingContext()));
            }
        }
    }

    public void clearIfExists(Long batchIndex){
        settlementDetailDao.clearIfExists(batchIndex);
    }

    public List<SettlementDetail> fetchSettlePaymentIds(int processCount){
        String status = "Open";
        List<SettlementDetailEntity> settlementDetails = settlementDetailDao.getByStatus(status, processCount);
        if(settlementDetails == null){
            return null;
        }
        List<SettlementDetail> settlementDetailList = new ArrayList<>();
        for(SettlementDetailEntity entity : settlementDetails){
            settlementDetailList.add(paymentMapper.toSettlementDetail(entity, new MappingContext()));
        }
        return settlementDetailList;
    }

    public void closeSettlement(SettlementDetail settlementDetail){
        SettlementDetailEntity detail = settlementDetailDao.get(Long.parseLong(settlementDetail.getMerchantReference()));
        detail.setStatus("Closed");
        settlementDetailDao.update(detail);
    }
}
