/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * payment instrument service.
 */
public interface PaymentInstrumentService {
    @Transactional
    Promise<PaymentInstrument> add(PaymentInstrument request);
    @Transactional
    void delete(Long paymentInstrumentId);
    @Transactional
    void update(PaymentInstrument request);
    @Transactional(readOnly = true)
    Promise<PaymentInstrument> getById(Long paymentInstrumentId);
    @Transactional(readOnly = true)
    List<PaymentInstrument> getByUserId(Long userId);
    @Transactional(readOnly = true)
    Promise<List<PaymentInstrument>> searchPi(Long userId, PaymentInstrumentSearchParam searchParam);
    @Transactional(readOnly = true)
    PaymentInstrumentType getPIType(String piType);
}
