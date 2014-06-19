/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.cloudant;

import com.junbo.billing.db.repo.TransactionRepository;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.TransactionId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;

import java.util.List;

/**
 * Created by haomin on 14-6-19.
 */
public class TransactionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<Transaction, TransactionId> implements TransactionRepository {
    @Override
    public Promise<List<Transaction>> getByBalanceId(Long balanceId) {
        return super.queryView("by_balance_id", balanceId.toString());
    }
}
