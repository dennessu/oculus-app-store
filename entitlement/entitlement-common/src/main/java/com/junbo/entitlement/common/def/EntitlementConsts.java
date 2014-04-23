/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.def;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
    public static final String NO_TYPE = "NO_TYPE";

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
}
