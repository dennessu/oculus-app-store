/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.sharding.repo.BaseRepository;

/**
 * Created by minhao on 6/16/14.
 */
public interface PaymentEventRepository extends BaseRepository<PaymentEvent, Long> {
}
