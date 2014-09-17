/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.facade;

import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;

import java.util.List;

/**
 * Created by minhao on 6/18/14.
 */
public interface PaymentInstrumentRepositoryFacade {
    PaymentInstrument getByPIId(Long piId);
    void save(PaymentInstrument request);
    void delete(Long paymentInstrumentId);
    void update(PaymentInstrument request);
    void updateExternalInfo(Long paymentInstrumentId, String externalToken, String label, String num);
    List<PaymentInstrument> getByUserId(Long userId);
    List<PaymentInstrument> search(Long userId, PaymentInstrumentSearchParam searchParam);
    String getFacebookPaymentAccount(Long userId);
    String createFBPaymentAccountIfNotExist(FacebookPaymentAccountMapping model);
}
