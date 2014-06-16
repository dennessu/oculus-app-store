/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.auth;

import com.junbo.authorization.AbstractAuthorizeCallback;
import com.junbo.common.id.UserId;
import com.junbo.payment.spec.model.PaymentInstrument;

/**
 * com.junbo.payment.auth.PaymentInstrumentAuthorizeCallback.
 */
public class PaymentInstrumentAuthorizeCallback extends AbstractAuthorizeCallback<PaymentInstrument> {
    PaymentInstrumentAuthorizeCallback(PaymentInstrumentAuthorizeCallbackFactory factory, PaymentInstrument entity) {
        super(factory, entity);
    }

    @Override
    public String getApiName() {
        return "payment-instruments";
    }

    @Override
    protected UserId getUserOwnerId() {
        PaymentInstrument entity = getEntity();
        if (entity != null) {
            return new UserId(getEntity().getUserId());
        }

        return null;
    }
}
