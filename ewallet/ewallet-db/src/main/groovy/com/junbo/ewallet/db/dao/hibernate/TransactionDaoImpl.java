/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.entity.TransactionEntity;
import org.hibernate.Query;

import java.util.List;
import java.util.UUID;

/**
 * Hibernate impl of TransactionDao.
 */
public class TransactionDaoImpl extends BaseDaoImpl<TransactionEntity> implements TransactionDao {
    @Override
    public List<TransactionEntity> getByWalletId(Long walletId) {
        String queryString = "from TransactionEntity where walletId = (:walletId)";
        Query q = currentSession(walletId).createQuery(queryString).setLong("walletId", walletId);
        return q.list();
    }

    @Override
    public TransactionEntity getByTrackingUuid(Long shardMasterId, UUID uuid) {
        String queryString = "from TransactionEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession(shardMasterId).createQuery(queryString).setParameter("trackingUuid", uuid);
        return (TransactionEntity) q.uniqueResult();
    }
}
