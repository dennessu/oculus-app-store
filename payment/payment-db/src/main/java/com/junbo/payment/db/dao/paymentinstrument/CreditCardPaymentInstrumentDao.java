/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.paymentinstrument;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;

/**
 * credit card dao.
 */
public class CreditCardPaymentInstrumentDao extends CommonDataDAOImpl<CreditCardPaymentInstrumentEntity, Long> {
    public CreditCardPaymentInstrumentDao() {
        super(CreditCardPaymentInstrumentEntity.class);
    }
}
