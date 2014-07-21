/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by xmchen on 14-3-13.
 */
public enum TaxStatus implements Identifiable<Short> {
    TAXED((short)0),
    NOT_TAXED((short)10),
    FAILED((short)-1),
    AUDITED((short)30);

    private final Short id;

    TaxStatus(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum TaxStatus not settable");
    }
}
