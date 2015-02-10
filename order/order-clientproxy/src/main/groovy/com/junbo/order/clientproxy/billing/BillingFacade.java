/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.billing;

import com.junbo.billing.spec.model.Balance;
import com.junbo.common.error.AppError;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Created by chriszhu on 2/19/14.
 */
public interface BillingFacade {
    Promise<Balance> createBalance(Balance balance, Boolean isAsyncCharge);
    Promise<Balance> settleBalance(Long balanceId);
    Promise<Balance> captureBalance(Balance balance);
    Promise<Balance> getBalanceById(Long balanceId);
    Promise<List<Balance>> getBalancesByOrderId(Long orderId);
    Promise<Balance> quoteBalance(Balance balance);
    Promise<Balance> confirmBalance(Balance balance);
    Promise<Balance> auditBalance(Balance balance);
    Promise<Balance> putBalance(Balance balance);

    AppError convertError(Throwable error);
}
