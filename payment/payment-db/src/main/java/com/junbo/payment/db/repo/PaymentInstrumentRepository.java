/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentInstrumentRepository extends BaseRepository<PaymentInstrument, Long> {
    @ReadMethod
    Promise<List<PaymentInstrument>> getByUserId(Long userId);
    @ReadMethod
    Promise<List<PaymentInstrument>> getByUserAndType(final Long userId, PIType piType);
}
