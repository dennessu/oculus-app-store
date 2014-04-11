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
public enum SubledgerItemAction implements Identifiable<Short> {

    CHARGE(0),
    REFUND(1);

    private SubledgerItemAction(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
