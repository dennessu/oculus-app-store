/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.dao.TransactionEntityDao;
import com.junbo.billing.db.entity.TransactionEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class TransactionEntityDaoImpl extends BaseDao implements TransactionEntityDao {
    @Override
    public TransactionEntity get(Long transactionId) {
        return (TransactionEntity)currentSession(transactionId).get(TransactionEntity.class, transactionId);
    }

    @Override
    public TransactionEntity save(TransactionEntity transaction) {
        transaction.setTransactionId(idGenerator.nextId(transaction.getBalanceId()));

        Session session = currentSession(transaction.getTransactionId());
        session.save(transaction);
        session.flush();
        return get(transaction.getTransactionId());
    }

    @Override
    public TransactionEntity update(TransactionEntity transaction) {

        Session session = currentSession(transaction.getTransactionId());
        session.merge(transaction);
        session.flush();

        return get(transaction.getTransactionId());
    }

    @Override
    public List<TransactionEntity> findByBalanceId(Long balanceId) {

        Criteria criteria = currentSession(balanceId).createCriteria(TransactionEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
