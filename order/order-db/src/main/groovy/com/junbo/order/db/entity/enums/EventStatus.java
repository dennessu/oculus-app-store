/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity.enums;

import com.junbo.common.util.Identifiable;

/**
 * Created by chriszhu on 2/25/14.
 */
public enum EventStatus implements Identifiable<Short> {
    OPEN(0),
    PROCESSING(1),
    PENDING(2),
    COMPLETED(100),
    FAILED(999),
    ERROR(-1);

    private EventStatus(int id) {
        this.id = (short) id;
    }
    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
