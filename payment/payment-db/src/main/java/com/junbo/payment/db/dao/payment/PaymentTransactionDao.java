/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.payment.PaymentTransactionEntity;

/**
 * payment dao.
 */
public class PaymentTransactionDao extends CommonDataDAOImpl<PaymentTransactionEntity, Long> {
    public PaymentTransactionDao() {
        super(PaymentTransactionEntity.class);
    }
}
