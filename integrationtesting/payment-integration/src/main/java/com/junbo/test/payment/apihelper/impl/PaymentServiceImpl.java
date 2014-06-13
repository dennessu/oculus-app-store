/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.impl;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Results;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.payment.apihelper.PaymentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public class PaymentServiceImpl extends HttpClientBase implements PaymentService {

    private static String paymentInstrumentUrl = ConfigHelper.getSetting("defaultCommerceEndpointV1");

    private LogHelper logger = new LogHelper(PaymentServiceImpl.class);

    private static PaymentService instance;

    public static synchronized PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentServiceImpl();
        }
        return instance;
    }

    @Override
    public String postPaymentInstrument(PaymentInstrument paymentInstrument) throws Exception {
        return postPaymentInstrument(paymentInstrument, 200);
    }

    @Override
    public String getPaymentInstrumentByPaymentId(String paymentInstrumentId) throws Exception {
        return getPaymentInstrumentByPaymentId(paymentInstrumentId, 200);
    }

    @Override
    public String getPaymentInstrumentByPaymentId(
            String paymentInstrumentId, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, paymentInstrumentUrl +
                "payment-instruments/" + paymentInstrumentId);

        PaymentInstrument paymentInstrumentResult = new JsonMessageTranscoder().decode(
                new TypeReference<PaymentInstrument>() {
                }, responseBody
        );

        paymentInstrumentId = IdConverter.idToUrlString(
                PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
        Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrumentResult);

        return paymentInstrumentId;

    }

    @Override
    public List<String> getPaymentInstrumentsByUserId(String uid) throws Exception {
        return getPaymentInstrumentsByUserId(uid, 200);
    }

    @Override
    public List<String> getPaymentInstrumentsByUserId(String uid, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, paymentInstrumentUrl +
                "payment-instruments?userId=" + uid, expectedResponseCode);

        Results<PaymentInstrument> paymentInstrumentResults = new JsonMessageTranscoder().decode(
                new TypeReference<Results<PaymentInstrument>>() {
                }, responseBody
        );

        List<String> paymentInstrumentList = new ArrayList<>();

        for (PaymentInstrument paymentInstrumentResult : paymentInstrumentResults.getItems()) {
            String paymentInstrumentId = IdConverter.idToUrlString(
                    PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
            paymentInstrumentList.add(paymentInstrumentId);
            Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrumentResult);
        }

        return paymentInstrumentList;
    }

    @Override
    public void creditWallet(CreditRequest creditRequest) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, paymentInstrumentUrl +
                "wallets/credit", creditRequest, 200);
    }

    @Override
    public String postPaymentInstrument(PaymentInstrument paymentInstrument,
                                        int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, paymentInstrumentUrl
                + "payment-instruments", paymentInstrument, expectedResponseCode);

        PaymentInstrument paymentInstrumentResult = new JsonMessageTranscoder().decode(
                new TypeReference<PaymentInstrument>() {
                }, responseBody
        );

        String paymentInstrumentId = IdConverter.idToUrlString(
                PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
        Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrumentResult);

        return paymentInstrumentId;
    }


    @Override
    public String updatePaymentInstrument(String uid, String paymentId,
                                          PaymentInstrument paymentInstrument) throws Exception {
        return updatePaymentInstrument(uid, paymentId, paymentInstrument, 200);
    }

    @Override
    public String updatePaymentInstrument(String uid, String paymentId, PaymentInstrument paymentInstrument,
                                          int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.PUT, paymentInstrumentUrl
                + "payment-instruments/" + paymentId, paymentInstrument, expectedResponseCode);

        PaymentInstrument paymentInstrumentResult = new JsonMessageTranscoder().decode(
                new TypeReference<PaymentInstrument>() {
                }, responseBody
        );

        String paymentInstrumentId = IdConverter.idToUrlString(
                PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
        Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrumentResult);

        return paymentInstrumentId;
    }

    @Override
    public void deletePaymentInstrument(String uid, String paymentId) throws Exception {
        this.deletePaymentInstrument(uid, paymentId, 204);
    }

    @Override
    public void deletePaymentInstrument(String uid, String paymentId, int expectedResponseCode) throws Exception {
        restApiCall(HTTPMethod.DELETE, paymentInstrumentUrl + "payment-instruments/" + paymentId, expectedResponseCode);
        Master.getInstance().removePaymentInstrument(paymentId);
    }


}
