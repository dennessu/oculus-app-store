/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletLotDao;
import com.junbo.ewallet.db.entity.WalletLotEntity;
import org.hibernate.Query;

import java.util.Date;
import java.util.List;

/**
 * Hibernate impl of WalletLotDao.
 */
public class WalletLotDaoImpl extends BaseDao<WalletLotEntity> implements WalletLotDao {
    @Override
    public void delete(Long id) {
        currentSession(id).delete(get(id));
    }

    public List<WalletLotEntity> getValidLot(Long walletId) {
        String queryString = "select * from ewallet_lot " +
                "where ewallet_id = (:walletId)" +
                " and remaining > 0" +
                " and (expiration_date is null or expiration_date >= (:now))" +
                " order by type desc";
        Query q = currentSession(walletId).createSQLQuery(queryString)
                .addEntity(WalletLotEntity.class)
                .setLong("walletId", walletId)
                .setDate("now", new Date());
        return q.list();
    }
}
