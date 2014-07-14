/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.facade;

import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentTransaction;

import java.util.List;

/**
 * Created by minhao on 6/18/14.
 */
public interface PaymentRepositoryFacade {
    void save(PaymentTransaction request);
    PaymentTransaction getByPaymentId(Long paymentId);
    void savePaymentEvent(Long paymentId, List<PaymentEvent> events);
    void updatePayment(Long paymentId, PaymentStatus status, String externalToken);
    List<PaymentEvent> getPaymentEventsByPaymentId(Long paymentId);
    void addPaymentProperties(Long paymentId, PaymentCallbackParams properties);
    PaymentCallbackParams getPaymentProperties(Long paymentId);
}
