/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;

import com.junbo.payment.db.dao.GenericDAOImpl;
import com.junbo.payment.db.entity.payment.PaymentProviderEntity;

/**
 * payment provider dao.
 */
public class PaymentProviderDao extends GenericDAOImpl<PaymentProviderEntity, Integer> {
    public PaymentProviderDao() {
        super(PaymentProviderEntity.class);
    }
}
