/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by chriszhu on 2/25/14.
 */
public enum FulfillmentEventType implements Identifiable<Short> {
    FULFILL(0),
    REVERSE_FULFILL(1),
    PREORDER(2),
    REQUEST_FULFILL(3),
    SHIP(4),
    REQUEST_SHIP(5);

    private FulfillmentEventType(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum FulfillmentEventType not settable");
    }

}
