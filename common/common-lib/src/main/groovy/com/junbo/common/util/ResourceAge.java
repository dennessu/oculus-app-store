/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

/**
 * The utility class to handle resourceAge.
 */
public class ResourceAge {
    private ResourceAge() { }

    public static String initial() {
        return encodeResourceAge(0);
    }

    public static boolean isNewer(String resourceAge1, String resourceAge2) {
        return parseResourceAge(resourceAge1) > parseResourceAge(resourceAge2);
    }

    public static String increment(String resourceAge) {
        return encodeResourceAge(parseResourceAge(resourceAge) + 10);
    }

    private static long parseResourceAge(String resourceAge) {
        // if (resourceAge != null && resourceAge.startsWith("R")) {
        //     return Long.parseLong(resourceAge.substring(1));
        // }
        if (resourceAge != null) {
            return Long.parseLong(resourceAge);
        }
        return 0;
    }

    private static String encodeResourceAge(long resourceAge) {
        // return "R" + resourceAge;
        return String.valueOf(resourceAge);
    }
}
