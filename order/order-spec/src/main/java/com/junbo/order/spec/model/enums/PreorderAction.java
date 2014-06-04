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
public enum PreorderAction implements Identifiable<Short> {
    PREORDER(0),
    CHARGE(1),
    FULFILL(2),
    PRE_RELEASE_NOTIFY(3),
    RELEASE_NOTIFY(4);

    private PreorderAction(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum PreorderAction not settable");
    }

}
