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
    public static final String FREE = "Free";
    public static final String TIER_PRICING = "TierPricing";
    public static final String NORMAL_PRICING = "NormalPricing";

    public static List<String> getPriceTypes() {
        return Arrays.asList(FREE, TIER_PRICING, NORMAL_PRICING);
    }

    private PriceType() {}
}
