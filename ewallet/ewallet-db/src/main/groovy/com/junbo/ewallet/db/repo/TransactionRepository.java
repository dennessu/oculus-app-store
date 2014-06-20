/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.entity.TransactionEntity;
import com.junbo.ewallet.db.mapper.ModelMapper;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * transaction repo.
 */
public class TransactionRepository {
    @Autowired
    private TransactionDao transactionDao;
    @Autowired
    private ModelMapper mapper;

    public List<Transaction> getTransactions(Long walletId) {
        List<TransactionEntity> results = transactionDao.getByWalletId(walletId);
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionEntity entity : results) {
            transactions.add(mapper.toTransaction(entity, new MappingContext()));
        }
        return transactions;
    }

    public Transaction getByTrackingUuid(Long shardMasterId, UUID uuid) {
        return mapper.toTransaction(transactionDao.getByTrackingUuid(shardMasterId, uuid), new MappingContext());
    }

    public Transaction get(long transactionId) {
        return mapper.toTransaction(transactionDao.get(transactionId), new MappingContext());
    }
}
