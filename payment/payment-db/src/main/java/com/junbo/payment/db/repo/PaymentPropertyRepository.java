/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentProperty;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentPropertyRepository extends BaseRepository<PaymentProperty, Long> {
    @ReadMethod
    Promise<List<PaymentProperty>> getByPaymentId(Long paymentId);
}
