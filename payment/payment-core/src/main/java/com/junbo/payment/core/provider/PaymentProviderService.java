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

    Promise<PaymentInstrument> add(PaymentInstrument request);
    Promise<Response> delete(String token);

    Promise<PaymentTransaction> authorize(String piToken, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> charge(String piToken, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest);
    Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request);
    List<PaymentTransaction> getByOrderId(String orderId);
    Promise<PaymentTransaction> getByTransactionToken(String token);
}
