/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.BalanceService;
import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.resource.BalanceResource;
import com.junbo.common.id.BalanceId;
import com.junbo.common.id.OrderId;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
@Scope("prototype")
public class BalanceResourceImpl implements BalanceResource {

    @Autowired
    private BalanceService balanceService;

    @Override
    public Promise<Balance> postBalance(Balance balance) {
        return balanceService.addBalance(balance);
    }

    @Override
    public Promise<Balance> quoteBalance(Balance balance) {
        return balanceService.quoteBalance(balance);
    }

    @Override
    public Promise<Balance> getBalance(BalanceId balanceId) {
        return balanceService.getBalance(balanceId.getValue());
    }

    @Override
    public Promise<List<Balance>> getBalances(OrderId orderId) {
        return balanceService.getBalances(orderId.getValue());
    }
}
