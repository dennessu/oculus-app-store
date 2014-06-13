/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by chriszhu on 6/12/14.
 */
public enum OrderItemRevisionType implements Identifiable<Short> {
    REFUND(0),
    ADJUST_TAX(1),
    ADJUST_SHIPPING(2),
    CANCEL(3);

    private OrderItemRevisionType(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum OrderItemRevisionType not settable");
    }

}
