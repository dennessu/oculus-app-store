/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.BalanceEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-21.
 */
public interface BalanceEntityDao {
    BalanceEntity get(Long balanceId);
    BalanceEntity save(BalanceEntity balance);
    BalanceEntity update(BalanceEntity balance, BalanceEntity oldBalance);
    List<BalanceEntity> getByTrackingUuid(UUID trackingUuid);
    List<BalanceEntity> getInitBalances();
    List<BalanceEntity> getAwaitingPaymentBalances();
    List<BalanceEntity> getUnconfirmedBalances();
    List<BalanceEntity> getRefundBalancesByOriginalId(Long balanceId);
}
