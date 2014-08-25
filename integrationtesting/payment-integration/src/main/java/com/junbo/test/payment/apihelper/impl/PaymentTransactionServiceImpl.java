/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.impl;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.payment.apihelper.PaymentTransactionService;
import com.junbo.common.json.JsonMessageTranscoder;

/**
 @author Jason
  * Time: 5/7/2014
  * The implementation for Payment transcation related APIs
 */
public class PaymentTransactionServiceImpl extends HttpClientBase implements PaymentTransactionService {

    private static String paymentTransactionUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/payment-transactions";

    private LogHelper logger = new LogHelper(PaymentServiceImpl.class);

    private static PaymentTransactionService instance;

    public static synchronized PaymentTransactionService getInstance() {
        if (instance == null) {
            instance = new PaymentTransactionServiceImpl();
        }
        return instance;
    }

    private PaymentTransactionServiceImpl() {
    }

    public PaymentTransaction authorize(PaymentTransaction request) throws Exception {
        return authorize(request, 200);
    }

    public PaymentTransaction authorize(PaymentTransaction request, int expectedResponseCode) throws Exception {
        String url = paymentTransactionUrl + "/authorization";
        String responseBody = restApiCall(HTTPMethod.POST, url, request, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<PaymentTransaction>() {}, responseBody);
    }

    public PaymentTransaction getPaymentTransaction(Long paymentId) throws Exception {
        return getPaymentTransaction(paymentId, 200);
    }
    public PaymentTransaction getPaymentTransaction(Long paymentId, int expectedResponseCode) throws Exception {
        String url = paymentTransactionUrl + "/" +IdConverter.idToUrlString(PaymentInstrumentId.class, paymentId);
        String responseBody = restApiCall(HTTPMethod.GET, url, null, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<PaymentTransaction>() {}, responseBody);
    }

    public PaymentTransaction charge(PaymentTransaction request) throws Exception {
        return charge(request, 200);
    }

    public PaymentTransaction charge(PaymentTransaction request, int expectedResponseCode) throws Exception {
        String url = paymentTransactionUrl + "/charge";
        String responseBody = restApiCall(HTTPMethod.POST, url, request, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<PaymentTransaction>() {}, responseBody);
    }

    public PaymentTransaction confirm(Long paymentTransactionId, PaymentTransaction request) throws Exception {
        return confirm(paymentTransactionId, request, 200);
    }

    public PaymentTransaction confirm(Long paymentTransactionId, PaymentTransaction request, int expectedResponseCode) throws Exception {
        String url = paymentTransactionUrl + "/" + IdConverter.idToUrlString(PaymentInstrumentId.class, paymentTransactionId) + "/confirm";
        String responseBody = restApiCall(HTTPMethod.POST, url, request, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<PaymentTransaction>() {}, responseBody);
    }

}
