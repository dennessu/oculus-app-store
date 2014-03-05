/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.dao.TransactionEntityDao;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.transaction.TransactionEntity;
import com.junbo.billing.spec.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by xmchen on 14-2-24.
 */
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private TransactionEntityDao transactionEntityDao;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        TransactionEntity entity = modelMapper.toTransactionEntity(transaction, new MappingContext());
        entity.setCreatedBy("BILLING");
        entity.setCreatedDate(new Date());
        Long id = transactionEntityDao.insert(entity);

        return getTransaction(id);
    }

    @Override
    public Transaction getTransaction(Long transactionId) {
        TransactionEntity entity = transactionEntityDao.get(transactionId);
        if(entity != null) {
            return modelMapper.toTransaction(entity, new MappingContext());
        }
        return null;
    }
}
