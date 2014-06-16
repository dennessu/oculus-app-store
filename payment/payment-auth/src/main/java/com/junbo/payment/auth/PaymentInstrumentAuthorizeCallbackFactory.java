/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.auth;

import com.junbo.authorization.AbstractAuthorizeCallbackFactory;
import com.junbo.authorization.AuthorizeCallback;
import com.junbo.payment.spec.model.PaymentInstrument;

/**
 * com.junbo.payment.auth.PaymentInstrumentAuthorizeCallbackFactory.
 */
public class PaymentInstrumentAuthorizeCallbackFactory extends AbstractAuthorizeCallbackFactory<PaymentInstrument> {
    @Override
    public AuthorizeCallback<PaymentInstrument> create(PaymentInstrument entity) {
        return new PaymentInstrumentAuthorizeCallback(this, entity);
    }

    public AuthorizeCallback<PaymentInstrument> create(Long userId) {
        PaymentInstrument entity = new PaymentInstrument();
        entity.setUserId(userId);
        return create(entity);
    }
}
