/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.rating.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by lizwu on 3/4/14.
 */
public class Constants {
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final BigDecimal PRICE_NOT_FOUND = null;
    public static final String DEFAULT_CURRENCY = "XXX";

    public static final Integer DEFAULT_PAGE_START = 0;
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final int UNIQUE = 0;

    //event & action type
    public static final String PURCHASE_EVENT = "PURCHASE";
    public static final String CYCLE_EVENT = "CYCLE";
    public static final String CANCEL_EVENT = "CANCEL";
    public static final String CHARGE_ACTION = "CHARGE";

    //action condition properties
    public static final String FROM_CYCLE = "FROM_CYCLE";
    public static final String TO_CYCLE = "TO_CYCLE";
    public static final String EXTEND_DURATION = "EXTEND_DURATION";
    public static final String EXTEND_DURATION_UNIT = "EXTEND_DURATION_UNIT";

    private Constants() {
    }
}
