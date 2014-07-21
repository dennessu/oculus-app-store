/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Item type.
 */
public enum ItemType {
    PHYSICAL, APP, DOWNLOADED_ADDITION, STORED_VALUE, SUBSCRIPTION, PERMANENT_UNLOCK, CONSUMABLE_UNLOCK;

    public static final List<ItemType> ALL = Arrays.asList(ItemType.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        try {
            ItemType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
