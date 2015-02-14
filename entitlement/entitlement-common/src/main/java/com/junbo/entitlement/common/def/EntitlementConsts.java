/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.def;

import com.junbo.catalog.spec.enums.EntitlementType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Consts for entitlement.
 */
public class EntitlementConsts {
    private EntitlementConsts() {
    }

    public static final int MAX_PAGE_SIZE = 200;
    public static final int DEFAULT_PAGE_SIZE = 200;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final Long NEVER_EXPIRE_LONG = 253402271999000L;   //DATE_FORMAT.parse("9999-12-31T23:59:59Z").getTime()
    public static final Long MIN_DATE = 0L;
    public static final Long MAX_DATE = NEVER_EXPIRE_LONG + 1;

    public static final String NO_TYPE = "NULL";
    public static final Date NEVER_EXPIRE = new Date(NEVER_EXPIRE_LONG);
    public static final Integer UNCONSUMABLE_USECOUNT = Integer.MAX_VALUE;
    public static final Integer MIN_USECOUNT = Integer.MIN_VALUE;

    public static final Set<String> ALLOWED_TYPE = new HashSet<String>() {{
        for (EntitlementType type : EntitlementType.values()) {
            add(type.toString());
        }
    }};
}
