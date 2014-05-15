/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by chriszhu on 2/25/14.
 */
public enum FulfillmentAction implements Identifiable<Short> {
    FULFILL(0),
    REVERSE(1),
    RETURN(2),
    PREORDER(3),
    SHIP(4),
    DELIVER(5),
    REPLACE(6);


    private FulfillmentAction(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum FulfillmentAction not settable");
    }

}
