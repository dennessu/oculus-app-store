/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.mapper.WalletMapper;
import com.junbo.ewallet.spec.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * transaction repo.
 */
public class TransactionRepository {
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private WalletMapper mapper;

    public List<Transaction> getTransactions(Long walletId) {
        List<TransactionEntity> results = transactionDao.getByWalletId(walletId);
        return mapper.toTransactions(results);
    }

    public Transaction getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return mapper.toTransaction(transactionDao.getByTrackingUuid(shardMasterId, uuid));
    }
}
