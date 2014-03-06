/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.spec.model.Transaction;

import java.util.List;

/**
 * Created by xmchen on 14-2-24.
 */
public interface TransactionRepository {
    Transaction saveTransaction(Transaction transaction);

    Transaction getTransaction(Long transactionId);

    List<Transaction> getTransactions(Long balanceId);
}
