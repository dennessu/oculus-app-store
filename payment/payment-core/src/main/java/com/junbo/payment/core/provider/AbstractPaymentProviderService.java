/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.spec.internal.CallbackParams;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;


/**
 * abstract provider service to minimize the override of interface.
 */
public abstract class AbstractPaymentProviderService implements PaymentProviderService{
    @Override
    public Promise<PaymentTransaction> confirm(String transactionId, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("confirm").exception();
    }

    @Override
    public Promise<PaymentTransaction> credit(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("credit").exception();
    }

    @Override
    public Promise<PaymentTransaction> confirmNotify(PaymentTransaction paymentRequest, CallbackParams properties){
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("confirmNotify").exception();
    }

    @Override
    public Promise<PaymentTransaction> processNotify(String request){
        throw AppServerExceptions.INSTANCE.serviceNotImplemented("processNotify").exception();
    }
}
