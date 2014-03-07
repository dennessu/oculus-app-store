/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.spec.model.Balance;

import java.util.List;

/**
 * Created by xmchen on 14-2-19.
 */
public interface BalanceRepository {
    Balance saveBalance(Balance balance);

    Balance getBalance(Long balanceId);

    List<Balance> getBalances(Long orderId);
}
