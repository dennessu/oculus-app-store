/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.hibernate.WalletLotEntity;

/**
 * Interface of WalletLotDao.
 */
public interface WalletLotDao {
    WalletLotEntity get(Long id);

    Long insert(WalletLotEntity walletLotEntity);

    Long update(WalletLotEntity walletLotEntity);

    void delete(Long id);
}
