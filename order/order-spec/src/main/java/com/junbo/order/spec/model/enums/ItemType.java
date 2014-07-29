/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model.enums;

import com.junbo.common.util.Identifiable;

import javax.ws.rs.NotSupportedException;

/**
 * Created by LinYi on 2/10/14.
 */
public enum ItemType implements Identifiable<Short> {
    DIGITAL(0),
    PHYSICAL_GOODS(1),
    GIFT_CARD(2),
    DOWNLOADABLE_SOFTWARE(3),
    DIGITAL_CONTENT(4),
    SHIPPING_AND_HANDLING(5);

    private ItemType(int id) {
        this.id = (short) id;
    }

    private Short id;

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public void setId(Short id) {
        throw new NotSupportedException("enum ItemType not settable");
    }

}
