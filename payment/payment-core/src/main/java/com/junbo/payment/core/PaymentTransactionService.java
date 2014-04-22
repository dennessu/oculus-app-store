/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.core;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.springframework.transaction.annotation.Transactional;

/**
 * payment transaction service.
 */
public interface PaymentTransactionService {
    @Transactional
    Promise<PaymentTransaction> authorize(PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> capture(Long paymentId, PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> confirm(Long paymentId, PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> charge(PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> reverse(Long paymentId, PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> refund(Long paymentId, PaymentTransaction request);
    @Transactional
    Promise<PaymentTransaction> getUpdatedTransaction(Long paymentId);
    @Transactional(readOnly = true)
    Promise<PaymentTransaction> getProviderTransaction(Long paymentId);
    @Transactional
    void reportPaymentEvent(PaymentEvent event);
}
