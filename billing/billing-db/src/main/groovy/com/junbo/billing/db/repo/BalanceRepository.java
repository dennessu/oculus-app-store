/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo;

import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by haomin on 14-6-9.
 */
public interface BalanceRepository extends BaseRepository<Balance, BalanceId> {
    @ReadMethod
    Promise<List<Balance>> getByTrackingUuid(UUID trackingUuid);

    @ReadMethod
    Promise<List<Balance>> getInitBalances();

    @ReadMethod
    Promise<List<Balance>> getAwaitingPaymentBalances();

    @ReadMethod
    Promise<List<Balance>> getUnconfirmedBalances();

    @ReadMethod
    Promise<List<Balance>> getRefundBalancesByOriginalId(Long balanceId);
}
