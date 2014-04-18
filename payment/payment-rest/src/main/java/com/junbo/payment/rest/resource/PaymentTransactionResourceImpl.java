/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;


import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.resource.PaymentTransactionResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * payment transaction resource implementation.
 */
public class PaymentTransactionResourceImpl implements PaymentTransactionResource{
    @Autowired
    private PaymentTransactionService paymentService;
    @Override
    public Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.authorize(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCapture(Long paymentId, PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.capture(paymentId, request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentConfirm(Long paymentId, PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.confirm(paymentId, request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.charge(request);
    }

    @Override
    public Promise<PaymentTransaction> reversePayment(Long paymentId, PaymentTransaction request) {
        return paymentService.reverse(paymentId, request);
    }

    @Override
    public Promise<PaymentTransaction> getPayment(Long paymentId) {
        return paymentService.getById(paymentId);
    }

    @Override
    public Promise<PaymentTransaction> getExternalPayment(Long paymentId) {
        return paymentService.getProviderTransaction(paymentId);
    }
}
