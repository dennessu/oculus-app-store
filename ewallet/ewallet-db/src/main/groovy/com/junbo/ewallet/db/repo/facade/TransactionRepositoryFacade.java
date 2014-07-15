/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.facade;

import com.junbo.ewallet.db.repo.TransactionRepository;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.langur.core.promise.SyncModeScope;
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
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.getByWalletId(walletId).syncGet();
        }
    }

    public Transaction getByTrackingUuid(Long shardMasterId, UUID uuid) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.getByTrackingUuid(shardMasterId, uuid).syncGet();
        }
    }

    public Transaction get(long transactionId) {
        try (SyncModeScope scope = new SyncModeScope()) {
            return transactionRepository.get(transactionId).syncGet();
        }
    }
}
