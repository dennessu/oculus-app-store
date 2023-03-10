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

import java.util.List;

/**
 * payment instrument resource implementation.
 */
public class PaymentInstrumentTypeResourceImpl implements PaymentInstrumentTypeResource {
    @Autowired
    private PaymentInstrumentService piService;

    @Override
    public Promise<PaymentInstrumentType> getById(String paymentInstrumentTypeId) {
        PaymentInstrumentType piType = piService.getPIType(paymentInstrumentTypeId);
        return Promise.pure(piType);
    }

    @Override
    public Promise<List<PaymentInstrumentType>> getAllTypes() {
        return null;
    }
}
