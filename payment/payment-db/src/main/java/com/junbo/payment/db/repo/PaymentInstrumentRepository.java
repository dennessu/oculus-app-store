/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;

import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentInstrumentRepository {

    void save(PaymentInstrument request);

    void delete(Long paymentInstrumentId);

    void update(PaymentInstrument request);

    void updateExternalInfo(Long paymentInstrumentId, String externalToken, String label, String num);

    PaymentInstrument getByPIId(Long piId);

    List<PaymentInstrument> getByUserId(Long userId);

    List<PaymentInstrument> search(Long userId, PaymentInstrumentSearchParam searchParam, PageMetaData pageMetadata);
}
