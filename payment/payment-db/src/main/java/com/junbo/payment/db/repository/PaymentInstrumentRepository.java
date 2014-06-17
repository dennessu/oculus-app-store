/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.common.id.PIType;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.db.dao.paymentinstrument.CreditCardPaymentInstrumentDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.spec.model.CreditCardDetail;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * payment instrument Repository.
 */
public class PaymentInstrumentRepository {
    @Autowired
    private PaymentInstrumentDao paymentInstrumentDao;
    @Autowired
    private CreditCardPaymentInstrumentDao ccPaymentInstrumentDao;
    @Autowired
    private PaymentMapper paymentMapperImpl;
    @Autowired
    private PaymentMapperExtension paymentMapperExtension;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    public void save(PaymentInstrument request){
        Long piId = null;
        PaymentInstrumentEntity piEntity = paymentMapperExtension.toPIEntity(request);
        if(request.getId() == null){
            piId = idGenerator.nextId(piEntity.getUserId());
        }else{
            piId = request.getId();
        }
        piEntity.setId(piId);
        paymentInstrumentDao.save(piEntity);
        if(PIType.get(request.getType()).equals(PIType.CREDITCARD)){
            CreditCardPaymentInstrumentEntity ccPiEntity = paymentMapperImpl.toCreditCardEntity(
                    (CreditCardDetail)paymentMapperExtension.toSpecificDetail(request.getTypeSpecificDetails(),
                            PIType.CREDITCARD), new MappingContext());
            ccPiEntity.setId(piId);
            ccPiEntity.setLastBillingDate(new Date());
            ccPaymentInstrumentDao.save(ccPiEntity);
        }
        request.setId(piId);
    }

    public void delete(Long paymentInstrumentId){
        PaymentInstrumentEntity entity = paymentInstrumentDao.get(paymentInstrumentId);
        entity.setDeleted(true);
        paymentInstrumentDao.update(entity);
    }

    public void update(PaymentInstrument request){
        PaymentInstrumentEntity pi = paymentInstrumentDao.get(request.getId());
        //setup column allowed to be updated:
        pi.setLabel(request.getLabel());
        pi.setAccountNum(request.getAccountNum());
        pi.setUserId(request.getUserId());
        pi.setBillingAddressId(request.getBillingAddressId());
        pi.setEmail(request.getEmail());
        pi.setAccountName(request.getAccountName());
        pi.setPhoneNumber(request.getPhoneNumber());
        pi.setRelationToHolder(request.getRelationToHolder());
        if(request.getIsActive() != null){
            pi.setIsActive(request.getIsActive());
        }
        paymentInstrumentDao.update(pi);
        if(PIType.get(request.getType()).equals(PIType.CREDITCARD)){
            CreditCardDetail creditCardDetail = paymentMapperExtension.toSpecificDetail(request.getTypeSpecificDetails(), PIType.CREDITCARD);
            CreditCardPaymentInstrumentEntity entity = ccPaymentInstrumentDao.get(pi.getId());
            // setup column allowed to be updated:
            entity.setExpireDate(creditCardDetail.getExpireDate());
            ccPaymentInstrumentDao.update(entity);
        }
    }

    public void updateExternalInfo(Long paymentInstrumentId, String externalToken, String label, String num){
        PaymentInstrumentEntity entity = paymentInstrumentDao.get(paymentInstrumentId);
        if(!CommonUtil.isNullOrEmpty(externalToken)){
            entity.setExternalToken(externalToken);
        }
        if(!CommonUtil.isNullOrEmpty(label)){
            entity.setLabel(entity.getLabel() == null ? label : entity.getLabel() + label);
        }
        if(!CommonUtil.isNullOrEmpty(num)){
            entity.setAccountNum(num);
        }
        paymentInstrumentDao.update(entity);
    }

    public PaymentInstrument getByPIId(Long piId){
        PaymentInstrumentEntity pi = paymentInstrumentDao.get(piId);
        if(pi == null || pi.isDeleted()){
            return null;
        }
        PaymentInstrument request = paymentMapperExtension.toPaymentInstrument(pi);
        setAdditionalInfo(pi, request);
        return request;
    }

    private void setAdditionalInfo(PaymentInstrumentEntity pi, PaymentInstrument request) {
        if(pi.getType().equals(PIType.CREDITCARD)){
            CreditCardPaymentInstrumentEntity ccPi = ccPaymentInstrumentDao.get(pi.getId());
            request.setTypeSpecificDetails(paymentMapperExtension.toTypeSpecificDetails(
                    paymentMapperImpl.toCreditCardDetail(ccPi, new MappingContext())));
        }
    }

    public List<PaymentInstrument> getByUserId(Long userId){
        List<PaymentInstrument> request = new ArrayList<PaymentInstrument>();
        List<PaymentInstrumentEntity> piEntities = paymentInstrumentDao.getByUserId(userId);
        for(PaymentInstrumentEntity piEntity : piEntities){
            if(!piEntity.isDeleted()){
                PaymentInstrument piRequest = paymentMapperExtension.toPaymentInstrument(piEntity);
                setAdditionalInfo(piEntity, piRequest);
                request.add(piRequest);
            }
        }
        return request;
    }

    public List<PaymentInstrument> search(Long userId,
            PaymentInstrumentSearchParam searchParam, PageMetaData pageMetadata) {
        List<PaymentInstrument> request = new ArrayList<PaymentInstrument>();
        List<PaymentInstrumentEntity> piEntities = paymentInstrumentDao.getByUserAndType(userId,
            CommonUtil.isNullOrEmpty(searchParam.getType()) ? null : PIType.valueOf(searchParam.getType()));
        for(PaymentInstrumentEntity piEntity : piEntities){
            if(!piEntity.isDeleted()){
                PaymentInstrument piRequest = paymentMapperExtension.toPaymentInstrument(piEntity);
                setAdditionalInfo(piEntity, piRequest);
                request.add(piRequest);
            }
        }
        return request;
    }
}
