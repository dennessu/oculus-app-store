/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.impl;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.payment.apihelper.PaymentCallbackService;
//import com.junbo.payment.spec.model.PaymentCallbackParams;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.LogHelper;

/**
 @author Jason
  * Time: 5/7/2014
  * The implementation for Payment callback related APIs
 */
public class PaymentCallbackServiceImpl extends HttpClientBase implements PaymentCallbackService {

    private static String paymentCallbackUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "payment-callback";

    private LogHelper logger = new LogHelper(PaymentServiceImpl.class);

    private static PaymentCallbackService instance;

    public static synchronized PaymentCallbackService getInstance() {
        if (instance == null) {
            instance = new PaymentCallbackServiceImpl();
        }
        return instance;
    }

    private PaymentCallbackServiceImpl() {
    }
    /*

    public void postPaymentProperties(String paymentId, PaymentCallbackParams properties) throws Exception {
        this.postPaymentProperties(paymentId, properties, 200);
    }

    public void postPaymentProperties(String paymentId, PaymentCallbackParams properties, int expectedResponseCode) throws Exception {
        String url = paymentCallbackUrl + "/" + paymentId + "/properties";
        restApiCall(HTTPMethod.POST, url, properties, expectedResponseCode);
    }
    */

}
