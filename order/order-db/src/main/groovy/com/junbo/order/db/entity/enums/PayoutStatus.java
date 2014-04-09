/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by fzhang on 4/2/2014.
 */
public enum PayoutStatus implements Identifiable<Short> {

    PENDING(0),
    COMPLETED(1),
    FAILED(2);

    private PayoutStatus(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
