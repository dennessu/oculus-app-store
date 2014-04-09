/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import java.util.*;

/**
 * Price Type.
 */
public class PriceType {
    public static final String FREE = "FREE";
    public static final String TIERED = "TIERED";
    public static final String CUSTOM = "CUSTOM";
    public static final Set<String> ALL_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FREE, TIERED, CUSTOM)));

    private PriceType() {}
}
