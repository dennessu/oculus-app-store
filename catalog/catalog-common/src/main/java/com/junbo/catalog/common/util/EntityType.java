/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

/**
 * Entity type.
 */
public enum EntityType {
    ITEM(1), OFFER(2);

    private final int value;
    EntityType(int value) { this.value = value; }
    public int getValue() { return value; }
}
