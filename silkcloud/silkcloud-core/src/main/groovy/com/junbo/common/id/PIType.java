/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * payment instrument type enum.
 */
public enum PIType {
    CREDITCARD(0L),
    STOREDVALUE(2L),
    PAYPAL(3L),
    OTHERS(4L);

    private final Long id;
    PIType(Long id){
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    // Lookup table
    private static final Map LOOKUP = new HashMap<Long, PIType>();

    // Populate the lookup table on loading time
    static {
        for (PIType s : EnumSet.allOf(PIType.class))
            LOOKUP.put(s.getId(), s);
    }

    // This method can be used for reverse lookup purpose
    public static PIType get(Long id) {
        return (PIType)LOOKUP.get(id);
    }

    public static String allTypes() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');

        Iterator<PIType> iter = LOOKUP.values().iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().name());
            if (iter.hasNext()) {
                sb.append('|');
            }
        }
        sb.append(']');

        return sb.toString();
    }
}
