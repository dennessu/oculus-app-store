/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.payment.impl;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.model.Results;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.testing.common.apihelper.HttpClientBase;
import com.junbo.testing.common.apihelper.payment.PaymentService;
import com.junbo.testing.common.blueprint.Master;
import com.junbo.testing.common.libs.IdConverter;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.libs.RestUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public class PaymentServiceImpl extends HttpClientBase implements PaymentService {

    private static String paymentInstrumentUrl = RestUrl.getRestUrl(RestUrl.ComponentName.PAYMENT);

    private LogHelper logger = new LogHelper(PaymentServiceImpl.class);

    private static PaymentService instance;

    public static synchronized PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentServiceImpl();
        }
        return instance;
    }

    @Override
    public String addPaymentInstrument(String uid, PaymentInstrument paymentInstrument) throws Exception {
        return addPaymentInstrument(uid, paymentInstrument, 200);
    }

    @Override
    public String addPaymentInstrument(String uid, PaymentInstrument paymentInstrument,
                                       int expectedResponseCode) throws Exception {
        PaymentInstrument paymentInstrumentResult =
                restApiCall(HTTPMethod.POST, paymentInstrumentUrl, paymentInstrument, expectedResponseCode);
        String paymentInstrumentId = IdConverter.idLongToHexString(
                PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
        Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrument);

        return paymentInstrumentId;
    }

    @Override
    public List<String> getPaymentInstrumentsByUserId(String uid) throws Exception {
        return getPaymentInstrumentsByUserId(uid, 200);
    }

    @Override
    public List<String> getPaymentInstrumentsByUserId(String uid, int expectedResponseCode) throws Exception {
        Results<PaymentInstrument> paymentInstrumentResults =
                restApiCall(HTTPMethod.GET, paymentInstrumentUrl + uid, expectedResponseCode);
        List<String> paymentInstrumentList = new ArrayList<>();

        for (PaymentInstrument paymentInstrument : paymentInstrumentResults.getItems()) {
            String paymentInstrumentId = IdConverter.idLongToHexString(
                    PaymentInstrumentId.class, paymentInstrument.getId().longValue());
            Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrument);
            paymentInstrumentList.add(paymentInstrumentId);
        }

        return paymentInstrumentList;
    }

    @Override
    public String updatePaymentInstrument(String uid, String paymentId,
                                          PaymentInstrument paymentInstrument) throws Exception {
        return updatePaymentInstrument(uid, paymentId, paymentInstrument, 200);
    }

    @Override
    public String updatePaymentInstrument(String uid, String paymentId, PaymentInstrument paymentInstrument,
                                          int expectedResponseCode) throws Exception {
        PaymentInstrument paymentInstrumentResult =
                restApiCall(HTTPMethod.PUT, paymentInstrumentUrl + uid, paymentInstrument, expectedResponseCode);
        String paymentInstrumentId =
                IdConverter.idLongToHexString(PaymentInstrumentId.class, paymentInstrumentResult.getId().longValue());
        Master.getInstance().addPaymentInstrument(paymentInstrumentId, paymentInstrument);

        return paymentInstrumentId;
    }

    @Override
    public void deletePaymentInstrument(String uid, String paymentId) throws Exception {
        this.deletePaymentInstrument(uid, paymentId);
    }

    @Override
    public void deletePaymentInstrument(String uid, String paymentId, int expectedResponseCode) throws Exception {
        restApiCall(HTTPMethod.DELETE, paymentInstrumentUrl + uid, expectedResponseCode);
        Master.getInstance().removePaymentInstrument(paymentId);
    }

    @Override
    public String searchPaymentInstrument(String uid) throws Exception {
        return null;
    }

    @Override
    public String searchPaymentInstrument(String uid, int expectedResponseCode) throws Exception {
        return null;
    }
}
