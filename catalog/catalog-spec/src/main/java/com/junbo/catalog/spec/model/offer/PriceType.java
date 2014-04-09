/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import java.util.Arrays;
import java.util.List;

/**
 * Price Type.
 */
public class PriceType {
    public static final String FREE = "FREE";
    public static final String TIERED = "TIERED";
    public static final String EXPLICIT = "EXPLICIT";

    public static List<String> getPriceTypes() {
        return Arrays.asList(FREE, TIERED, EXPLICIT);
    }

    public static boolean isValidType(String priceType) {
        for (String type : getPriceTypes()) {
            if (type.equals(priceType)) {
                return true;
            }
        }
        return false;
    }

    private PriceType() {}
}
