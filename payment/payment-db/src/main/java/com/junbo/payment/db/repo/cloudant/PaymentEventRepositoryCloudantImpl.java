/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.PaymentEventRepository;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class PaymentEventRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<PaymentEvent, Long> implements PaymentEventRepository {
    @Override
    public Promise<List<PaymentEvent>> getByPaymentId(Long paymentId) {
        return super.queryView("by_payment_id", paymentId.toString());
    }
}
