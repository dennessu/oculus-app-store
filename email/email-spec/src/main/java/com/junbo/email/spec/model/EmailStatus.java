/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Email Status.
 */
public enum EmailStatus implements Identifiable<Short> {
    PENDING((short)1),
    SUCCEED((short)2),
    FAILED((short)3),
    SCHEDULED((short)4);

    EmailStatus(Short id) {
        this.id = id;
    }

    private final Short id;

    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum EmailStatus not settable");
    }

}
