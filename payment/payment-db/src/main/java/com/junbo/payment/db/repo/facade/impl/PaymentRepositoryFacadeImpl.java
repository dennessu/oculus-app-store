/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.facade.impl;

import com.junbo.langur.core.promise.SyncModeScope;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.db.repo.PaymentEventRepository;
import com.junbo.payment.db.repo.PaymentPropertyRepository;
import com.junbo.payment.db.repo.PaymentTransactionRepository;
import com.junbo.payment.db.repo.facade.PaymentRepositoryFacade;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentProperty;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by minhao on 6/18/14.
 */
public class PaymentRepositoryFacadeImpl implements PaymentRepositoryFacade {
    private PaymentTransactionRepository paymentTransactionRepository;
    private PaymentEventRepository paymentEventRepository;
    private PaymentPropertyRepository paymentPropertyRepository;

    @Required
    public void setPaymentTransactionRepository(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    @Required
    public void setPaymentEventRepository(PaymentEventRepository paymentEventRepository) {
        this.paymentEventRepository = paymentEventRepository;
    }

    @Required
    public void setPaymentPropertyRepository(PaymentPropertyRepository paymentPropertyRepository) {
        this.paymentPropertyRepository = paymentPropertyRepository;
    }

    public void save(PaymentTransaction request){
        try (SyncModeScope scope = new SyncModeScope()) {
            PaymentTransaction saved = paymentTransactionRepository.create(request).syncGet();
            request.setId(saved.getId());
            savePaymentEvent(saved.getId(), request.getPaymentEvents());
        }
    }

    public PaymentTransaction getByPaymentId(Long paymentId){
        try (SyncModeScope scope = new SyncModeScope()) {
            return paymentTransactionRepository.get(paymentId).syncGet();
        }
    }

    public void savePaymentEvent(Long paymentId, List<PaymentEvent> events){
        try (SyncModeScope scope = new SyncModeScope()) {
            for (PaymentEvent event : events) {
                event.setPaymentId(paymentId);
                paymentEventRepository.create(event).syncGet();
            }
        }
    }

    public void updatePayment(Long paymentId, PaymentStatus status, String externalToken){
        try (SyncModeScope scope = new SyncModeScope()) {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.get(paymentId).syncGet();
            if (status != null) {
                paymentTransaction.setStatus(status.toString());
            }
            if (externalToken != null) {
                paymentTransaction.setExternalToken(externalToken);
            }
            paymentTransactionRepository.update(paymentTransaction).syncGet();
        }
    }

    public List<PaymentEvent> getPaymentEventsByPaymentId(Long paymentId){
        try (SyncModeScope scope = new SyncModeScope()) {
            return paymentEventRepository.getByPaymentId(paymentId).syncGet();
        }
    }

    public void addPaymentProperties(Long paymentId, PaymentCallbackParams properties){
        try (SyncModeScope scope = new SyncModeScope()) {
            if (properties == null) {
                return;
            }
            Map<String, String> mapProperties = CommonUtil.parseJson(CommonUtil.toJson(properties, null), HashMap.class);
            for (final Map.Entry property : mapProperties.entrySet()) {
                PaymentProperty paymentProperty = new PaymentProperty();
                paymentProperty.setPaymentId(paymentId);
                paymentProperty.setPropertyName(property.getKey().toString());
                paymentProperty.setPropertyValue(property.getValue().toString());
                paymentPropertyRepository.create(paymentProperty).syncGet();
            }
        }
    }

    public PaymentCallbackParams getPaymentProperties(Long paymentId){
        try (SyncModeScope scope = new SyncModeScope()) {
            PaymentTransaction paymentTransaction = paymentTransactionRepository.get(paymentId).syncGet();
            if (paymentTransaction == null) {
                return null;
            }
            List<PaymentProperty> properties = paymentPropertyRepository.getByPaymentId(paymentId).syncGet();
            if (properties == null) {
                return null;
            }
            Map<String, String> paymentProperties = new HashMap<>();
            for (PaymentProperty property : properties) {
                paymentProperties.put(property.getPropertyName(), property.getPropertyValue());
            }
            return CommonUtil.parseJson(CommonUtil.toJson(paymentProperties, null), PaymentCallbackParams.class);
        }
    }
}
