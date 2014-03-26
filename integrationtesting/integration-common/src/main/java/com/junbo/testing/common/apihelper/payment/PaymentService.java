/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.apihelper.payment;

import com.junbo.payment.spec.model.PaymentInstrument;

import java.util.List;

/**
 * Created by Yunlong on 3/24/14.
 */
public interface PaymentService {
    String postPaymentInstrumentToUser(String uid, PaymentInstrument paymentInstrument) throws Exception;

    String postPaymentInstrumentToUser(String uid, PaymentInstrument paymentInstrument,
                                int expectedResponseCode) throws Exception;

    List<String> getPaymentInstrumentsByUserId(String uid) throws Exception;

    List<String> getPaymentInstrumentsByUserId(String uid, int expectedResponseCode) throws Exception;

    String updatePaymentInstrument(String uid, String paymentId, PaymentInstrument paymentInstrument) throws Exception;

    String updatePaymentInstrument(String uid, String paymentId, PaymentInstrument paymentInstrument,
                                   int expectedResponseCode) throws Exception;


    void deletePaymentInstrument(String uid, String paymentId) throws Exception;

    void deletePaymentInstrument(String uid, String paymentId, int expectedResponseCode) throws Exception;

    String searchPaymentInstrument(String uid) throws Exception;

    String searchPaymentInstrument(String uid, int expectedResponseCode) throws Exception;

}
