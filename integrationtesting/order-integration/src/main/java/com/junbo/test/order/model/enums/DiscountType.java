/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model.enums;

/**
 * Enum for discount type.
 *
 * @author Yunlong
 *         Created on 6/5/2014.
 */

public enum DiscountType {
    OFFER_DISCOUNT("OFFER_DISCOUNT"),
    ORDER_DISCOUNT("ORDER_DISCOUNT");

    private String name;

    private DiscountType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
