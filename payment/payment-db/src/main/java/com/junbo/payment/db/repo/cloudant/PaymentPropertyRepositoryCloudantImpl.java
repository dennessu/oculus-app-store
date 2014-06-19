/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.PaymentPropertyRepository;
import com.junbo.payment.spec.model.PaymentProperty;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class PaymentPropertyRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<PaymentProperty, Long> implements PaymentPropertyRepository{
    @Override
    public Promise<List<PaymentProperty>> getByPaymentId(Long paymentId) {
        return super.queryView("by_payment_id", paymentId.toString());
    }
}
