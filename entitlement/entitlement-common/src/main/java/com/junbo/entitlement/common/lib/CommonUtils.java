/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.lib;

import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.def.Function;

import javax.ws.rs.core.UriBuilder;
import java.util.HashSet;
import java.util.Set;

/**
 * Common Util.
 */
public class CommonUtils {
    private CommonUtils() {
    }

    public static UriBuilder buildPageParams(UriBuilder builder, Integer start, Integer count) {
        if (start == null) {
            start = EntitlementConsts.DEFAULT_PAGE_NUMBER;
        }
        if (count == null) {
            count = EntitlementConsts.DEFAULT_PAGE_SIZE;
        }
        return builder.queryParam("start", start + count).queryParam("count", count);
    }

    public static Boolean isNotNull(Object o) {
        if (o == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public static <T, U> Set<U> select(Set<T> values, Function<U, T> function) {
        Set<U> result = new HashSet<U>();
        for (T t : values) {
            result.add(function.apply(t));
        }
        return result;
    }
}
