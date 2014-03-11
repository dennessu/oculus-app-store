/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.hibernate.WalletEntity;

/**
 * Interface of WalletDao.
 */
public interface WalletDao {
    WalletEntity get(Long id);

    WalletEntity update(WalletEntity walletEntity);

    WalletEntity insert(WalletEntity walletEntity);
}
