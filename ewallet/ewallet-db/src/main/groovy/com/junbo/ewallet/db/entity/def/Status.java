/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity.def;

import com.junbo.common.util.Identifiable;

/**
 * Enum for status.
 */
public enum Status implements Identifiable<Integer> {
    ACTIVE(1),
    LOCKED(-1),
    EXPIRED(-2);

    private Integer id;

    Status(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
