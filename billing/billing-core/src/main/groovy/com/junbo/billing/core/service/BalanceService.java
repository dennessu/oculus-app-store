/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
public interface BalanceService {

    Promise<Balance> addBalance(Balance balance);

    Promise<Balance> quoteBalance(Balance balance);

    Promise<Balance> captureBalance(Balance balance);

    Promise<Balance> confirmBalance(Balance balance);

    Promise<Balance> checkBalance(Balance balance);

    Promise<Balance> processAsyncBalance(Balance balance);

    Promise<Balance> getBalance(BalanceId balanceId);

    Promise<List<Balance>> getBalances(OrderId orderId);

    Promise<Balance> putBalance(Balance balance);

    Promise<Balance> auditBalance(Balance balance);
}
