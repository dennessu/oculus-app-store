/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.entity;

import com.junbo.common.util.Identifiable;

/**
 * Created by fzhang@wan-san.com on 14-1-24.
 */
public enum ItemStatus implements Identifiable<Short> {

    OPEN(0),

    DELETED(1);

    private ItemStatus(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }
}
