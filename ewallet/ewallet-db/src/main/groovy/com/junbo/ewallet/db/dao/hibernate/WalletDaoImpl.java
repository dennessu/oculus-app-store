/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.entity.def.Currency;
import com.junbo.ewallet.db.entity.def.WalletType;
import com.junbo.ewallet.db.entity.hibernate.WalletEntity;
import org.hibernate.Query;

import java.util.UUID;

/**
 * Hibernate impl of WalletDao.
 */
public class WalletDaoImpl extends BaseDao<WalletEntity> implements WalletDao{
    @Override
    public WalletEntity getByTrackingUuid(UUID uuid) {
        String queryString = "from WalletEntity where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", uuid);
        return (WalletEntity) q.uniqueResult();
    }

    @Override
    public WalletEntity get(Long userId, WalletType type, Currency currency) {
        String queryString = "from WalletEntity where userId = (:userId) and type = (:type)";
        if(currency != null){
            queryString += " and currency = (:currency)";
        }
        Query q = currentSession().createQuery(queryString)
                .setLong("userId", userId)
                .setInteger("type", type.getId());
        if(currency != null){
            q.setInteger("currency", currency.getId());
        }
        return (WalletEntity) q.uniqueResult();
    }
}
