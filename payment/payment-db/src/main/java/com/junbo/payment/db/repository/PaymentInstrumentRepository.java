/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.paymentinstrument.AddressDao;
import com.junbo.payment.db.dao.paymentinstrument.CreditCardPaymentInstrumentDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.paymentinstrument.*;
import com.junbo.payment.db.mapper.CreditCardDetail;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.model.*;
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
    private AddressDao addressDao;
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
        request.setRev("1");
        PaymentInstrumentEntity piEntity = paymentMapperExtension.toPIEntity(request);
        Long piId = idGenerator.nextId(piEntity.getUserId());
        if(request.getAddress() != null){
            AddressEntity address = paymentMapperImpl.toAddressEntity(request.getAddress(), new MappingContext());
            address.setId(piId);
            addressDao.save(address);
            piEntity.setAddressId(piId);
        }
        piEntity.setId(piId);
        paymentInstrumentDao.save(piEntity);
        if(request.getType().equals(PIType.CREDITCARD.toString())){
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
        if(request.getAddress().getId() != null){
            addressDao.update(paymentMapperImpl.toAddressEntity(request.getAddress(), new MappingContext()));
        }
        PaymentInstrumentEntity pi = paymentMapperExtension.toPIEntity(request);
        pi.setAddressId(request.getAddress().getId());
        Long currentRev = Long.parseLong(request.getRev()) + 1;
        pi.setRev(currentRev.toString());
        paymentInstrumentDao.update(pi);
        if(request.getType().equals(PIType.CREDITCARD.toString())){
            ccPaymentInstrumentDao.update(paymentMapperImpl.toCreditCardEntity(
                    (CreditCardDetail)paymentMapperExtension.toSpecificDetail(request.getTypeSpecificDetails(),
                            PIType.CREDITCARD), new MappingContext()));
        }
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
        if(pi.getAddressId() != null){
            AddressEntity address = addressDao.get(pi.getAddressId());
            request.setAddress(paymentMapperImpl.toAddress(address, new MappingContext()));
        }
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
        return getByUserId(userId);
    }
}
