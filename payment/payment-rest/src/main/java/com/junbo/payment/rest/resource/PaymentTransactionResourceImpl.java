/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;


import com.junbo.authorization.AuthorizeContext;
import com.junbo.common.error.AppCommonErrors;
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
public class PaymentTransactionResourceImpl implements PaymentTransactionResource{
    private static final String PAYMENT_SERVICE_SCOPE = "payment.service";
    @Autowired
    private PaymentTransactionService paymentService;

    @Override
    public Promise<PaymentTransaction> postPaymentCredit(PaymentTransaction request) {
        authorize();
        CommonUtil.preValidation(request);
        return paymentService.credit(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request) {
        authorize();
        CommonUtil.preValidation(request);
        return paymentService.authorize(request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCapture(PaymentId paymentId, PaymentTransaction request) {
        authorize();
        CommonUtil.preValidation(request);
        return paymentService.capture(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentConfirm(PaymentId paymentId, PaymentTransaction request) {
        authorize();
        CommonUtil.preValidation(request);
        return paymentService.confirm(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request) {
        authorize();
        CommonUtil.preValidation(request);
        return paymentService.charge(request);
    }

    @Override
    public Promise<PaymentTransaction> reversePayment(PaymentId paymentId, PaymentTransaction request) {
        authorize();
        return paymentService.reverse(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> refundPayment(PaymentId paymentId, PaymentTransaction request) {
        authorize();
        return paymentService.refund(paymentId.getValue(), request);
    }

    @Override
    public Promise<PaymentTransaction> getPayment(PaymentId paymentId) {
        authorize();
        return paymentService.getTransaction(paymentId.getValue());
    }

    @Override
    public Promise<PaymentTransaction> checkPaymentStatus(PaymentId paymentId) {
        authorize();
        return paymentService.getUpdatedTransaction(paymentId.getValue());
    }

    private static void authorize() {
        if (!AuthorizeContext.hasScopes(PAYMENT_SERVICE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }
    }
}
