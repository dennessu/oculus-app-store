/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;

import com.junbo.payment.spec.model.PaymentInstrumentType;

/**
 * PI Type Dao.
 */
public class PaymentInstrumentTypeDao extends GenericDAOImpl<PaymentInstrumentType, Integer> {
    public PaymentInstrumentTypeDao(){
        super(PaymentInstrumentType.class);
    }
}
