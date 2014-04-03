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

import java.util.UUID;

/**
 * Hibernate impl of WalletDao.
 */
public class WalletDaoImpl extends BaseDao<WalletEntity> implements WalletDao {
    @Override
    public WalletEntity getByTrackingUuid(UUID uuid) {
        String queryString = "from WalletEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", uuid);
        return (WalletEntity) q.uniqueResult();
    }

    @Override
    public WalletEntity get(Long userId, WalletType type, String currency) {
        String queryString = "from WalletEntity where userId = (:userId) and type = (:type) and currency = (:currency)";
        Query q = currentSession().createQuery(queryString)
                .setLong("userId", userId)
                .setInteger("type", type.getId())
                .setString("currency", currency);
        return (WalletEntity) q.uniqueResult();
    }
}
