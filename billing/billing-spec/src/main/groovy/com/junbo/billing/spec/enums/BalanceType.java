/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by xmchen on 14-2-27.
 */
public enum BalanceType implements Identifiable<Short> {
    DEBIT((short)1),
    MANUAL_CAPTURE((short)2),
    CREDIT((short)4),
    REFUND((short)8);

    private final Short id;

    BalanceType(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }
}
