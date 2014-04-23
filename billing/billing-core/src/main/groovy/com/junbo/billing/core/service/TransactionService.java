/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Balance;
import com.junbo.langur.core.promise.Promise;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-2-19.
 */
public interface TransactionService {

    Promise<Balance> processBalance(Balance balance);

    Promise<Balance> captureBalance(Balance balance, BigDecimal amount);

    Promise<Balance> confirmBalance(Balance balance);
}
