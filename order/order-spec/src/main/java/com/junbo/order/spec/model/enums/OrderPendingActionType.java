/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by fzhang on 2015/2/2.
 */
public enum OrderPendingActionType implements Identifiable<Short> {

    FB_TRANSACTION_RECEIVE(0),
    RISK_REVIEW(1);

    private OrderPendingActionType(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum OrderPendingActionType not settable");
    }

}
