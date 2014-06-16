/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentProperties;
import com.junbo.payment.spec.model.PaymentTransaction;

import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentRepository {
    void save(PaymentTransaction request);

    PaymentTransaction getByPaymentId(Long paymentId);

    void savePaymentEvent(Long paymentId, List<PaymentEvent> events);

    void updatePayment(Long paymentId, PaymentStatus status, String externalToken);

    List<PaymentEvent> getPaymentEventsByPaymentId(Long paymentId);

    void addPaymentProperties(Long paymentId, PaymentProperties properties);

    PaymentProperties getPaymentProperties(Long paymentId);
}
