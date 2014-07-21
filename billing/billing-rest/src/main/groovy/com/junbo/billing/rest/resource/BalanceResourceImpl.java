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
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
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
    public Promise<Balance> captureBalance(Balance balance) {
        return balanceService.captureBalance(balance);
    }

    @Override
    public Promise<Balance> confirmBalance(Balance balance) {
        return balanceService.confirmBalance(balance);
    }

    @Override
    public Promise<Balance> checkBalance(Balance balance) {
        return balanceService.checkBalance(balance);
    }

    @Override
    public Promise<Balance> processAsyncBalance(Balance balance) {
        return balanceService.processAsyncBalance(balance);
    }

    @Override
    public Promise<Balance> getBalance(BalanceId balanceId) {
        return balanceService.getBalance(balanceId);
    }

    @Override
    public Promise<Results<Balance>> getBalances(OrderId orderId) {
        return balanceService.getBalances(orderId)
                .then(new Promise.Func<List<Balance>, Promise<Results<Balance>>>() {
            @Override
            public Promise<Results<Balance>> apply(List<Balance> balances) {
                Results<Balance> results = new Results<>();
                results.setItems(balances);
                return Promise.pure(results);
            }
        });
    }

    @Override
    public Promise<Balance> putBalance(Balance balance) {
        return balanceService.putBalance(balance);
    }

    @Override
    public Promise<Balance> auditBalance(Balance balance) {
        return balanceService.auditBalance(balance);
    }
}
