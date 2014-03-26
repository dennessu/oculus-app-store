/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.common.model.Results;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentType;

import java.util.List;

class PaymentInstrumentResultList extends Results<PaymentInstrument> {
    @Override
    public List<PaymentInstrument> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<PaymentInstrument> results) {
        super.setItems(results);
    }
}
class PaymentInstrumentTypeResultList extends Results<PaymentInstrumentType> {
    @Override
    public List<PaymentInstrumentType> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<PaymentInstrumentType> results) {
        super.setItems(results);
    }
}
