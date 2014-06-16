/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.db.dao.payment.PaymentDao;
import com.junbo.payment.db.dao.payment.PaymentEventDao;
import com.junbo.payment.db.dao.payment.PaymentPropertyDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.payment.PaymentPropertyEntity;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentProperties;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * payment Repository.
 */
public class PaymentRepository {
    @Autowired
    private PaymentInstrumentDao piDao;
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private PaymentEventDao paymentEventDao;
    @Autowired
    private PaymentPropertyDao paymentPropertyDao;

    @Autowired
    private PaymentMapperExtension paymentMapperExtension;

    public void save(PaymentTransaction request){
        Long paymentId = paymentDao.save(paymentMapperExtension.toPaymentEntity(request));
        request.setId(paymentId);
        savePaymentEvent(paymentId, request.getPaymentEvents());
    }

    public PaymentTransaction getByPaymentId(Long paymentId){
        PaymentEntity entity = paymentDao.get(paymentId);
        //PaymentInstrumentEntity pi = piDao.get(entity.getPaymentInstrumentId());
        PaymentTransaction transaction = paymentMapperExtension.toPayment(entity);
        //transaction.getPaymentInstrumentId().setUserId(pi.getUserId());
        return transaction;
    }

    public void savePaymentEvent(Long paymentId, List<PaymentEvent> events){
        for(PaymentEvent event : events){
            event.setPaymentId(paymentId);
            paymentEventDao.save(paymentMapperExtension.toPaymentEventEntity(event));
        }
    }

    public void updatePayment(Long paymentId, PaymentStatus status, String externalToken){
        PaymentEntity entity = paymentDao.get(paymentId);
        if(status != null){
            entity.setStatusId(status.getId());
        }
        if(externalToken != null){
            entity.setExternalToken(externalToken);
        }
        paymentDao.update(entity);
    }

    public List<PaymentEvent> getPaymentEventsByPaymentId(Long paymentId){
        List<PaymentEvent> events = new ArrayList<PaymentEvent>();
        List<PaymentEventEntity> entities = paymentEventDao.getByPaymentId(paymentId);
        for(PaymentEventEntity entity : entities){
            events.add(paymentMapperExtension.toPaymentEvent(entity));
        }
        return events;
    }

    public void addPaymentProperties(Long paymentId, PaymentProperties properties){
        if(properties == null){
            return;
        }
        Map<String, String> mapProperties = CommonUtil.parseJson(CommonUtil.toJson(properties, null), HashMap.class);
        for(final Map.Entry property : mapProperties.entrySet()){
            PaymentPropertyEntity entity = new PaymentPropertyEntity();
            entity.setPaymentId(paymentId);
            entity.setPropertyName(property.getKey().toString());
            entity.setPropertyValue(property.getValue().toString());
            paymentPropertyDao.save(entity);
        }
    }

    public PaymentProperties getPaymentProperties(Long paymentId){
        PaymentEntity entity = paymentDao.get(paymentId);
        if(entity == null){
            return null;
        }
        List<PaymentPropertyEntity> properties = paymentPropertyDao.getByPaymentId(paymentId);
        if(properties == null){
            return  null;
        }
        Map<String, String> paymentProperties = new HashMap<>();
        for(PaymentPropertyEntity property : properties){
            paymentProperties.put(property.getPropertyName(), property.getPropertyValue());
        }
        return CommonUtil.parseJson(CommonUtil.toJson(paymentProperties, null), PaymentProperties.class);
    }
}
