/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;


import com.junbo.common.id.PaymentId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.resource.PaymentTransactionResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * payment transaction resource implementation.
 */
public class PaymentTransactionResourceImpl implements PaymentTransactionResource {
    @Autowired
    private PaymentTransactionService paymentService;

    @Override
    public Promise<PaymentTransaction> postPaymentCredit(PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.credit(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.authorize(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCapture(PaymentId paymentId, PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.capture(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentConfirm(PaymentId paymentId, PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.confirm(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        CommonUtil.preValidation(request);
        return paymentService.charge(request);
    }

    @Override
    public Promise<PaymentTransaction> reversePayment(PaymentId paymentId, PaymentTransaction request) {
        return paymentService.reverse(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> refundPayment(PaymentId paymentId, PaymentTransaction request) {
        return paymentService.refund(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> getPayment(PaymentId paymentId) {
        return paymentService.getTransaction(paymentId.getValue());
    }

    @Override
    public Promise<PaymentTransaction> checkPaymentStatus(PaymentId paymentId) {
        return paymentService.getUpdatedTransaction(paymentId.getValue());
    }
}
