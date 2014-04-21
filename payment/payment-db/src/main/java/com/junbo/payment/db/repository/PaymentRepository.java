/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;

import com.junbo.payment.db.dao.payment.PaymentDao;
import com.junbo.payment.db.dao.payment.PaymentEventDao;
import com.junbo.payment.db.dao.payment.PaymentPropertyDao;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.payment.PaymentPropertyEntity;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PropertyField;
import com.junbo.payment.spec.model.PaymentEvent;
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
            entity.setStatus(status);
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

    public void addPaymentProperties(Long paymentId, Map<PropertyField, String> properties){
        if(properties == null){
            return;
        }
        for(final Map.Entry property : properties.entrySet()){
            PaymentPropertyEntity entity = new PaymentPropertyEntity();
            entity.setPaymentId(paymentId);
            entity.setPropertyName(property.getKey().toString());
            entity.setPropertyValue(property.getValue().toString());
            paymentPropertyDao.save(entity);
        }
    }

    public Map<PropertyField, String> getPaymentProperties(Long paymentId){
        PaymentEntity entity = paymentDao.get(paymentId);
        if(entity == null){
            return null;
        }
        List<PaymentPropertyEntity> properties = paymentPropertyDao.getByPaymentId(paymentId);
        if(properties == null){
            return  null;
        }
        Map<PropertyField, String> paymentProperties = new HashMap<>();
        for(PaymentPropertyEntity property : properties){
            paymentProperties.put(PropertyField.valueOf(property.getPropertyName()), property.getPropertyValue());
        }
        return paymentProperties;
    }
}
