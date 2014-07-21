/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.TransactionEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
public interface TransactionEntityDao {
    TransactionEntity get(Long transactionId);
    TransactionEntity save(TransactionEntity transaction);
    TransactionEntity update(TransactionEntity transaction, TransactionEntity oldTransaction);
    List<TransactionEntity> findByBalanceId(Long balanceId);
}
