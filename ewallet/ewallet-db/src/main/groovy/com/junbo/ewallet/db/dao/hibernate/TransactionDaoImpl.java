/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.dao.hibernate;

import com.junbo.ewallet.db.dao.TransactionDao;
import com.junbo.ewallet.db.entity.hibernate.TransactionEntity;

import java.util.List;

/**
 * Hibernate impl of TransactionDao.
 */
public class TransactionDaoImpl extends TransactionBaseDao<TransactionEntity> implements TransactionDao{
    @Override
    public List<TransactionEntity> getByWalletId(Long walletId) {
        return null;
    }
}
