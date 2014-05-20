/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.def;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Consts for entitlement.
 */
public class EntitlementConsts {
    private EntitlementConsts() {
    }

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final String NEXT_END = "END";

    public static final String NO_TYPE = "NULL";
    public static final Date NEVER_EXPIRE = new Date(253402185600000L);
    public static final Integer UNCONSUMABLE_USECOUNT = Integer.MAX_VALUE;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final String MIN_DATE = "1900-01-01T00:00:00Z";
    public static final String MAX_DATE = "9999-12-31T23:59:59Z";

    public static final Set<String> ALLOWED_TYPE = new HashSet<>(Arrays.asList(new String[]{"DOWNLOAD", "RUN", "DEVELOPER"}));
}
