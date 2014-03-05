/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

/**
 * Predicate.
 */
public enum Predicate {
    INCLUDE_CATEGORY(0),
    EXCLUDE_CATEGORY(1),
    INCLUDE_OFFER(2),
    EXCLUDE_OFFER(3),
    ORDER_ITEM_COUNT_ABOVE(4),
    ORDER_ABSOLUTE_VALUE_ABOVE(5),
    HAS_COUPON(6),
    INCLUDE_ENTITLEMENT(7),
    EXCLUDE_ENTITLEMENT(8);

    private int sequence;

    private Predicate(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }
}
