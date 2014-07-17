/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.facade;

import com.junbo.ewallet.db.repo.TransactionRepository;
import com.junbo.ewallet.spec.model.Transaction;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.UUID;

/**
 * transaction repo.
 */
public class TransactionRepositoryFacade {
    private TransactionRepository transactionRepository;

    @Required
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactions(Long walletId) {
        return transactionRepository.getByWalletId(walletId).get();
    }

    public Transaction getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return transactionRepository.getByTrackingUuid(shardMasterId, uuid).get();
    }

    public Transaction get(long transactionId) {
        return transactionRepository.get(transactionId).get();
    }
}
