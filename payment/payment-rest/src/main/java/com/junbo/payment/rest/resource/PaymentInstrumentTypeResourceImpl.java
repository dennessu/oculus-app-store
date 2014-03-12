/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.rest.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import com.junbo.payment.spec.resource.PaymentInstrumentTypeResource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * payment instrument resource implementation.
 */
public class PaymentInstrumentTypeResourceImpl implements PaymentInstrumentTypeResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Override
    public Promise<PaymentInstrumentType> getById(String paymentInstrumentType) {
        PaymentInstrumentType piType = piService.getPIType(paymentInstrumentType);
        return Promise.pure(piType);
    }
}
