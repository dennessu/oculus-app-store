/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.listener.clientproxy;

import com.junbo.billing.spec.model.Balance;
import com.junbo.langur.core.promise.Promise;

/**
 * Created by xmchen on 14-4-11.
 */
public interface BillingFacade {
    Promise<Balance> processAsyncBalance(Balance balance);
    Promise<Balance> checkBalance(Balance balance);
}
