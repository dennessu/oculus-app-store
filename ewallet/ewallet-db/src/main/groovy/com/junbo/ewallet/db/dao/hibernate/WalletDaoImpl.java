/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.WalletDao;
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
}
