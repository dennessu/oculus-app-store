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
    // to be deleted: DIGITAL & VIRTUAL
    // DIGITAL --> APP & DOWNLOADED_ADDITION
    // VIRTUAL --> IN_APP_LOCK & IN_APP_CONSUMABLE
    DIGITAL, VIRTUAL,
    PHYSICAL, APP, DOWNLOADED_ADDITION, STORED_VALUE, SUBSCRIPTION, IN_APP_UNLOCK, IN_APP_CONSUMABLE;

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
