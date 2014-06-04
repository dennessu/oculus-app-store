/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.WalletType;
import org.hibernate.Query;

import java.util.List;
import java.util.UUID;

/**
 * Hibernate impl of WalletDao.
 */
public class WalletDaoImpl extends BaseDao<WalletEntity> implements WalletDao {
    @Override
    public WalletEntity getByTrackingUuid(Long shardMasterId, UUID uuid) {
        String queryString = "from WalletEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession(shardMasterId).createQuery(queryString).setParameter("trackingUuid", uuid);
        return (WalletEntity) q.uniqueResult();
    }

    @Override
    public WalletEntity get(Long userId, WalletType type, String currency) {
        String queryString = "from WalletEntity where userId = (:userId) and type = (:type) and currency = (:currency)";
        Query q = currentSession(userId).createQuery(queryString)
                .setLong("userId", userId)
                .setInteger("type", type.getId())
                .setString("currency", currency.toUpperCase());
        return (WalletEntity) q.uniqueResult();
    }

    @Override
    public List<WalletEntity> getAll(Long userId) {
        String queryString = "from WalletEntity where userId = (:userId)";
        Query q = currentSession(userId).createQuery(queryString).setParameter("userId", userId);
        return q.list();
    }
}
