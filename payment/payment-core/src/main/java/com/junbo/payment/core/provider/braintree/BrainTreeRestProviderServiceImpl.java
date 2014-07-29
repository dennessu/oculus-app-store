/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.braintree;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.internal.BrainTreeResource;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * brain tree rest implementation.
 */
public class BrainTreeRestProviderServiceImpl extends AbstractPaymentProviderService {
    private static final String PROVIDER_NAME = "BrainTree";
    private BrainTreeResource brainTreeResource;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {
        target.setAccountNumber(source.getAccountNumber());
        target.setExternalToken(source.getExternalToken());
        target.getTypeSpecificDetails().setCreditCardType(source.getTypeSpecificDetails().getCreditCardType());
        target.getTypeSpecificDetails().setCommercial(source.getTypeSpecificDetails().getCommercial());
        target.getTypeSpecificDetails().setDebit(source.getTypeSpecificDetails().getDebit());
        target.getTypeSpecificDetails().setPrepaid(source.getTypeSpecificDetails().getPrepaid());
        target.getTypeSpecificDetails().setIssueCountry(source.getTypeSpecificDetails().getIssueCountry());
    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {
        target.setExternalToken(source.getExternalToken());
        if(!CommonUtil.isNullOrEmpty(source.getStatus())){
            target.setStatus(source.getStatus());
        }
    }

    @Override
    public Promise<PaymentInstrument> add(PaymentInstrument request) {
        return brainTreeResource.addPaymentInstrument(request);
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return brainTreeResource.deletePaymentInstrument(pi.getExternalToken());
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(String token) {
        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        return brainTreeResource.postAuthorization(pi.getExternalToken(), paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return brainTreeResource.postCapture(transactionId, paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        return brainTreeResource.postCharge(pi.getExternalToken(), paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        return brainTreeResource.reverse(transactionId, paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        return Promise.pure(request);
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction request) {
        return Promise.pure(null);
    }

    public void setBrainTreeResource(BrainTreeResource brainTreeResource) {
        this.brainTreeResource = brainTreeResource;
    }
}
