/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Balance;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
public interface BalanceService {

    Promise<Balance> quoteBalance(Balance balance);

    Promise<Balance> addBalance(Balance balance);

    Promise<Balance> getBalance(Long balanceId);

    Promise<List<Balance>> getBalances(Long orderId);
}
