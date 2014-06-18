/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao.cloudant;

import com.junbo.ewallet.db.dao.WalletDao;
import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.WalletType;

import java.util.List;
import java.util.UUID;

/**
 * cloudantImpl of WalletDao.
 */
public class WalletDaoImpl extends BaseDao<WalletEntity> implements WalletDao {

    @Override
    public WalletEntity getByTrackingUuid(Long shardMasterId, UUID uuid) {
        List<WalletEntity> results = super.queryView("byTrackingUuid", uuid.toString()).get();
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public WalletEntity get(Long userId, WalletType type, String currency) {
        String key = userId.toString() + ":" + type.toString() + ":" + currency;
        List<WalletEntity> results = super.queryView("byUserIdAndTypeAndCurrency", key).get();
        return results.size() == 0 ? null : results.get(0);
    }

    @Override
    public List<WalletEntity> getAll(Long userId) {
        return super.queryView("byUserId", userId.toString()).get();
    }
}
