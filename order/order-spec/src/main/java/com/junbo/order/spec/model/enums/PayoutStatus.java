/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by fzhang on 4/2/2014.
 */
public enum PayoutStatus implements Identifiable<Short> {

    PENDING(0), // Pending Aggregation
    COMPLETED(1), // Subledger Paid
    FAILED(2), // Subledger Payout Failed
    PROCESSING(3); // In Process of Payout

    private PayoutStatus(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum PayoutStatus not settable");
    }

}
