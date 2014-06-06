/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper;

import com.junbo.payment.spec.model.PaymentProperties;

/**
 @author Jason
  * Time: 5/7/2014
  * The interface for Payment transcation related APIs
 */
public interface PaymentCallbackService {
    void postPaymentProperties(Long paymentId, PaymentProperties properties) throws Exception;
    void postPaymentProperties(Long paymentId, PaymentProperties properties, int expectedResponseCode) throws Exception;
}
