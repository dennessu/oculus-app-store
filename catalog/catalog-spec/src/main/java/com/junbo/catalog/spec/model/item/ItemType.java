/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Item type.
 */
public final class ItemType {
    public static final String PHYSICAL = "PHYSICAL";
    public static final String DIGITAL = "DIGITAL";
    public static final String WALLET = "WALLET";
    public static final String SUBSCRIPTION = "SUBSCRIPTION";
    public static final Set<String> ALL_TYPES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(PHYSICAL, DIGITAL, WALLET, SUBSCRIPTION)));

    private ItemType() {}
}
