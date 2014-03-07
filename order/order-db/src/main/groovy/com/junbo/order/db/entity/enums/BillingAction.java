/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by chriszhu on 2/25/14.
 */
public enum BillingAction implements Identifiable<Short> {
    CHARGE(0),
    AUTHORIZE(1),
    CREDIT(2),
    REFUND(3);

    private BillingAction(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
