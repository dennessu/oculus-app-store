/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.WalletLotEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface of WalletLotDao.
 */
public interface WalletLotDao {
    WalletLotEntity get(Long id);

    WalletLotEntity insert(WalletLotEntity walletLotEntity);

    WalletLotEntity update(WalletLotEntity walletLotEntity);

    void delete(Long id);

    List<WalletLotEntity> getValidLot(Long walletId);

    BigDecimal getValidAmount(Long walletId);
}
