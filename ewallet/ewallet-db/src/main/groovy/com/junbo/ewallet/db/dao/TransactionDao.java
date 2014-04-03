/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.TransactionEntity;

import java.util.List;

/**
 * Interface of TransactionDao.
 */
public interface TransactionDao {
    TransactionEntity get(Long id);

    TransactionEntity insert(TransactionEntity transactionEntity);

    List<TransactionEntity> getByWalletId(Long walletId);
}
