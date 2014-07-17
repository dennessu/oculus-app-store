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
        return transactionRepository.create(transaction).get();
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        return transactionRepository.get(new TransactionId(transactionId)).get();
    }

    @Override
    public List<Transaction> getTransactions(Long balanceId) {
        return transactionRepository.getByBalanceId(balanceId).get();
    }

    @Override
    public Transaction updateTransaction(Transaction transaction, Transaction oldTransaction) {
        return transactionRepository.update(transaction, oldTransaction).get();
    }
}
