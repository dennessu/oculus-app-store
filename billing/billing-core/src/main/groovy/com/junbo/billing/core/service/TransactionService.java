/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.Balance;

import java.math.BigDecimal;

/**
 * Created by xmchen on 14-2-19.
 */
public interface TransactionService {

    Balance processBalance(Balance balance);

    Balance captureBalance(Balance balance, BigDecimal amount);
}
