/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.billing.db.repo.sql;

import com.junbo.billing.db.dao.TransactionEntityDao;
import com.junbo.billing.db.entity.TransactionEntity;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.repo.TransactionRepository;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.TransactionId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haomin on 14-6-9.
 */
public class TransactionRepositorySqlImpl implements TransactionRepository {
    private TransactionEntityDao transactionEntityDao;
    private ModelMapper modelMapper;

    @Required
    public void setTransactionEntityDao(TransactionEntityDao transactionEntityDao) {
        this.transactionEntityDao = transactionEntityDao;
    }

    @Required
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Promise<Transaction> create(Transaction model) {
        TransactionEntity entity = modelMapper.toTransactionEntity(model, new MappingContext());
        TransactionEntity saved = transactionEntityDao.save(entity);
        return get(new TransactionId(saved.getId()));
    }

    @Override
    public Promise<Transaction> get(TransactionId id) {
        TransactionEntity entity = transactionEntityDao.get(id.getValue());
        if (entity != null) {
            return Promise.pure(modelMapper.toTransaction(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<Transaction> update(Transaction model, Transaction oldModel) {
        TransactionEntity entity = modelMapper.toTransactionEntity(model, new MappingContext());
        TransactionEntity oldEntity = modelMapper.toTransactionEntity(oldModel, new MappingContext());
        transactionEntityDao.update(entity, oldEntity);

        return get(new TransactionId(entity.getId()));
    }

    @Override
    public Promise<Void> delete(TransactionId id) {
        throw new UnsupportedOperationException("transaction not support delete");
    }

    @Override
    public Promise<List<Transaction>> getByBalanceId(Long balanceId) {
        List<TransactionEntity> list = transactionEntityDao.findByBalanceId(balanceId);
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionEntity entity : list) {
            Transaction transaction = modelMapper.toTransaction(entity, new MappingContext());
            if (transaction != null) {
                transactions.add(transaction);
            }
        }

        return Promise.pure(transactions);
    }
}
