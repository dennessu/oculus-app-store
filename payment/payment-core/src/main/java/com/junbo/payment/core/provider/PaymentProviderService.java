/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * payment provider service.
 */
public interface PaymentProviderService{
    String getProviderName();
    void clonePIResult(PaymentInstrument source, PaymentInstrument target);
    void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target);

    Promise<PaymentInstrument> add(PaymentInstrument request);
    Promise<Response> delete(PaymentInstrument pi);

    Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> charge(PaymentInstrument pi, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request);
    List<PaymentTransaction> getByBillingRefId(String orderId);
    Promise<PaymentTransaction> getByTransactionToken(String token);

    Promise<PaymentTransaction> confirm(String transactionId, PaymentTransaction paymentRequest);
}
