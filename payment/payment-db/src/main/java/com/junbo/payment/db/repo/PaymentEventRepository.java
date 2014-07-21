/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentEventRepository extends BaseRepository<PaymentEvent, Long> {
    @ReadMethod
    Promise<List<PaymentEvent>> getByPaymentId(final Long paymentId);
}
