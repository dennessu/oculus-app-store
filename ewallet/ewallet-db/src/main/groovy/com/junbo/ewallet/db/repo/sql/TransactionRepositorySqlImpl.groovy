/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo.sql

import com.junbo.ewallet.db.dao.TransactionDao
import com.junbo.ewallet.db.entity.TransactionEntity
import com.junbo.ewallet.db.mapper.ModelMapper
import com.junbo.ewallet.db.repo.TransactionRepository
import com.junbo.ewallet.spec.model.Transaction
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 6/23/14.
 */
@CompileStatic
class TransactionRepositorySqlImpl implements TransactionRepository {
    private TransactionDao transactionDao
    private ModelMapper modelMapper

    @Required
    void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<Transaction> get(Long id) {
        TransactionEntity entity = transactionDao.get(id)
        if (entity != null) {
            return Promise.pure(modelMapper.toTransaction(entity, new MappingContext()))
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Transaction> create(Transaction model) {
        TransactionEntity entity = modelMapper.toTransactionEntity(model, new MappingContext())
        Long savedId = transactionDao.insert(entity)
        return get(savedId)
    }

    @Override
    Promise<Transaction> update(Transaction model) {
        TransactionEntity entity = modelMapper.toTransactionEntity(model, new MappingContext())
        transactionDao.update(entity)
        return get(model.getId())
    }

    @Override
    Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on wallet transaction not support")
    }

    @Override
    Promise<List<Transaction>> getByWalletId(Long walletId) {
        List<TransactionEntity> list = transactionDao.getByWalletId(walletId)
        List<Transaction> transactionArrayList = new ArrayList<>()
        for (TransactionEntity entity : list) {
            Transaction transaction = modelMapper.toTransaction(entity, new MappingContext())
            if (transaction != null) {
                transactionArrayList.add(transaction)
            }
        }

        return Promise.pure(transactionArrayList)
    }

    @Override
    Promise<Transaction> getByTrackingUuid(Long shardMasterId, UUID uuid) {
        TransactionEntity entity = transactionDao.getByTrackingUuid(shardMasterId, uuid)
        return Promise.pure(modelMapper.toTransaction(entity, new MappingContext()))
    }
}
