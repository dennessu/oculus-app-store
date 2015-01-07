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
public enum EventType {
    PURCHASE, CYCLE, CANCEL;

    public static final List<EventType> ALL = Arrays.asList(EventType.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        if (type == null) {
            return false;
        }
        try {
            EventType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
