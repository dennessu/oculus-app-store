/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper;

import com.junbo.payment.spec.model.PaymentTransaction;

/**
 @author Jason
  * Time: 5/7/2014
  * The interface for Payment transcation related APIs
 */
public interface PaymentTransactionService {
    PaymentTransaction authorize(PaymentTransaction request) throws Exception;
    PaymentTransaction authorize(PaymentTransaction request, int expectedResponseCode) throws Exception;
    PaymentTransaction getPaymentTransaction(Long paymentId) throws Exception;
    PaymentTransaction getPaymentTransaction(Long paymentId, int expectedResponseCode) throws Exception;
    PaymentTransaction charge(PaymentTransaction request) throws Exception;
    PaymentTransaction charge(PaymentTransaction request, int expectedResponseCode) throws Exception;
    PaymentTransaction confirm(Long paymenTransactiontId, PaymentTransaction request) throws Exception;
    PaymentTransaction confirm(Long paymenTransactiontId, PaymentTransaction request, int expectedResponseCode) throws Exception;
}
