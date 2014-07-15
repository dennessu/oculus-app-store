/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repo.facade.impl;

import com.junbo.billing.db.repo.TransactionRepository;
import com.junbo.billing.db.repo.facade.TransactionRepositoryFacade;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.TransactionId;
import com.junbo.langur.core.promise.SyncModeScope;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * Created by xmchen on 14-2-24.
 */
public class TransactionRepositoryFacadeImpl implements TransactionRepositoryFacade {
    private TransactionRepository transactionRepository;

    @Required
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.create(transaction).syncGet();
        }
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.get(new TransactionId(transactionId)).syncGet();
        }
    }

    @Override
    public List<Transaction> getTransactions(Long balanceId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.getByBalanceId(balanceId).syncGet();
        }
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.update(transaction).syncGet();
        }
    }
}
