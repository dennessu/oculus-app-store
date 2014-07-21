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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
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
        if (transaction.getId() == null) {
            transaction.setId(idGenerator.nextId(transaction.getBalanceId()));
        }
        transaction.setCreatedTime(new Date());
        if (transaction.getCreatedBy() == null) {
            transaction.setCreatedBy("0");
        }
        Session session = currentSession(transaction.getId());
        session.save(transaction);
        session.flush();
        return get(transaction.getId());
    }

    @Override
    public TransactionEntity update(TransactionEntity transaction, TransactionEntity oldTransaction) {
        transaction.setUpdatedTime(new Date());
        if (transaction.getUpdatedBy() == null) {
            transaction.setUpdatedBy("0");
        }
        Session session = currentSession(transaction.getId());
        session.merge(transaction);
        session.flush();

        return get(transaction.getId());
    }

    @Override
    public List<TransactionEntity> findByBalanceId(Long balanceId) {

        Criteria criteria = currentSession(balanceId).createCriteria(TransactionEntity.class)
                .add(Restrictions.eq("balanceId", balanceId))
                .addOrder(Order.asc("createdTime"));
        return criteria.list();
    }
}
