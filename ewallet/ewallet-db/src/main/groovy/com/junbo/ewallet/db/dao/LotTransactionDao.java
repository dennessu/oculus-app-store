/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.dao;

import com.junbo.ewallet.db.entity.LotTransactionEntity;

import java.util.List;

/**
 * interface of LotTransactionDao.
 */
public interface LotTransactionDao extends BaseDao<LotTransactionEntity> {
    List<LotTransactionEntity> getByTransactionId(Long transactionId);
}
