/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by xmchen on 14-3-5.
 */
public enum TransactionStatus implements Identifiable<Short> {

    SUCCESS((short)1),
    DECLINE((short)2),

    ERROR((short)999);

    private final Short id;

    TransactionStatus(Short id) {
        this.id = id;
    }

    @Override
    public Short getId() {
        return id;
    }
}
