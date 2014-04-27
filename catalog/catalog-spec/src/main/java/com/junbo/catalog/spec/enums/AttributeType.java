/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Event type.
 */
public enum AttributeType {
    CATEGORY, GENRE;

    public static final List<AttributeType> ALL = Arrays.asList(AttributeType.values());

    public boolean equals(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        try {
            AttributeType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
