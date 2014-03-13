/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.db.dao.TransactionEventEntityDao;
import com.junbo.billing.db.transaction.TransactionEventEntity;
import com.junbo.common.id.Id;
import com.junbo.common.id.TransactionId;
import com.junbo.oom.core.MappingContext;
import com.junbo.billing.db.dao.TransactionEntityDao;
import com.junbo.billing.db.mapper.ModelMapper;
import com.junbo.billing.db.transaction.TransactionEntity;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xmchen on 14-2-24.
 */
public class TransactionRepositoryImpl implements TransactionRepository {

    @Autowired
    private TransactionEntityDao transactionEntityDao;

    @Autowired
    private TransactionEventEntityDao transactionEventEntityDao;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        TransactionEntity entity = modelMapper.toTransactionEntity(transaction, new MappingContext());

        entity.setTransactionId(idGenerator.nextId(TransactionId.class, entity.getBalanceId()));
        entity.setCreatedBy("BILLING");
        entity.setCreatedDate(new Date());
        Long id = transactionEntityDao.insert(entity);

        transactionEntityDao.flush();

        saveTransactionEventEntity(entity);
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

    @Override
    public List<Transaction> getTransactions(Long balanceId) {
        List<TransactionEntity> transactionEntities = transactionEntityDao.findByBalanceId(balanceId);
        List<Transaction> transactions = new ArrayList<>();
        for(TransactionEntity transactionEntity : transactionEntities) {
            Transaction transaction = modelMapper.toTransaction(transactionEntity, new MappingContext());
            if(transaction != null) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        TransactionEntity entity = modelMapper.toTransactionEntity(transaction, new MappingContext());
        TransactionEntity savedEntity = transactionEntityDao.get(entity.getTransactionId());

        savedEntity.setTypeId(entity.getTypeId());
        savedEntity.setStatusId(entity.getStatusId());
        savedEntity.setAmount(entity.getAmount());
        savedEntity.setModifiedBy("BILLING");
        savedEntity.setModifiedDate(new Date());
        transactionEntityDao.update(savedEntity);

        transactionEntityDao.flush();

        saveTransactionEventEntity(savedEntity);

        return getTransaction(entity.getTransactionId());
    }

    private void saveTransactionEventEntity(TransactionEntity transactionEntity) {
        TransactionEventEntity transactionEventEntity = new TransactionEventEntity();
        transactionEventEntity.setEventId(idGenerator.nextId(Id.class, transactionEntity.getTransactionId()));
        transactionEventEntity.setTransactionId(transactionEntity.getTransactionId());
        transactionEventEntity.setActionTypeId(transactionEntity.getTypeId());
        transactionEventEntity.setStatusId(transactionEntity.getStatusId());
        transactionEventEntity.setAmount(transactionEntity.getAmount());
        transactionEventEntity.setEventDate(new Date());

        transactionEventEntityDao.insert(transactionEventEntity);
        transactionEventEntityDao.flush();
    }
}
