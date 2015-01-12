/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Item subtype.
 */
public enum ItemSubtype {
    DOWNLOADABLE_ADDITION,VIDEO, PHOTO;

    public static final List<ItemSubtype> ALL = Arrays.asList(ItemSubtype.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        if (type==null) {
            return false;
        }
        try {
            ItemSubtype.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
