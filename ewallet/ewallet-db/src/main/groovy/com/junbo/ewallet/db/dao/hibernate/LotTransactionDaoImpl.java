/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.LotTransactionDao;
import com.junbo.ewallet.db.entity.LotTransactionEntity;
import org.hibernate.Query;

import java.util.List;

/**
 * LotTransactionDao Impl.
 */
public class LotTransactionDaoImpl extends TransactionBaseDao<LotTransactionEntity> implements LotTransactionDao {
    @Override
    public List<LotTransactionEntity> getByTransactionId(Long transactionId) {
        String queryString = "select * from lot_transaction" +
                " where transaction_id = (:transactionId)" +
                " and unrefunded_amount > 0" +
                " order by wallet_lot_type asc";
        Query q = currentSession(transactionId).createSQLQuery(queryString)
                .addEntity(this.getEntityType())
                .setLong("transactionId", transactionId);
        return q.list();
    }
}
