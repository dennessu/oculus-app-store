package com.junbo.subscription.common.util;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class Utils {
    private Utils() {

    }

    public static Date now() {
        return new Date();
    }

    public static boolean checkString(String input) {
        return input != null && input.trim().length() > 0;
    }

    public static boolean equals(String a, String b) {
        if (a != null)
            return a.equalsIgnoreCase(b);

        return b == null;
    }

}