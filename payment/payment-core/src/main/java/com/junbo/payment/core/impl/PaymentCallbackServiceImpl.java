/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.db.repository.PaymentRepository;
import com.junbo.payment.spec.enums.PropertyField;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * payment callback service implementation.
 */
public class PaymentCallbackServiceImpl implements PaymentCallbackService{
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCallbackServiceImpl.class);
    @Autowired
    private PaymentRepository paymentRepository;
    @Override
    public void addPaymentProperties(Long paymentId, Map<PropertyField, String> properties) {
        PaymentTransaction existedTransaction = paymentRepository.getByPaymentId(paymentId);
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid.");
            throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
        }
        paymentRepository.addPaymentProperties(paymentId, properties);
    }
}
