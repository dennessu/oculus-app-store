/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.WalletEntity;
import com.junbo.ewallet.spec.def.WalletType;

import java.util.List;
import java.util.UUID;

/**
 * Interface of WalletDao.
 */
public interface WalletDao {
    WalletEntity get(Long id);

    WalletEntity update(WalletEntity walletEntity);

    WalletEntity insert(WalletEntity walletEntity);

    WalletEntity getByTrackingUuid(Long shardMasterId, UUID uuid);

    WalletEntity get(Long userId, WalletType type, String currency);

    List<WalletEntity> getAll(Long userId);
}
