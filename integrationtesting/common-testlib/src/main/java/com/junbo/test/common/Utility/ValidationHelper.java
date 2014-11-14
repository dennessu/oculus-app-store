/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Utility;

import com.junbo.test.common.exception.TestException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Yunlong on 4/4/14.
 */
public abstract class ValidationHelper {
    public ValidationHelper() {
    }

    public static void verifyEqual(BigDecimal actual, BigDecimal expect, String message) {
        if (actual.doubleValue() != expect.doubleValue()) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(int actual, int expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(long actual, long expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(boolean actual, boolean expect, String message) {
        if (actual != expect) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(List<String> actual, List<String> expect, String message) {
        if (actual.size() != expect.size() || !(actual.equals(expect))) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

    public static void verifyEqual(String actual, String expect, String message) {
        verifyEqual(actual, expect, true, true, message);
    }

    public static void verifyEqual(
            String actual, String expect, boolean ignoreCase, boolean ignoreWhitespace, String message) {
        boolean sameString = false;
        if (ignoreCase && !ignoreWhitespace) {
            sameString = expect.equalsIgnoreCase(actual);
        } else if (!ignoreCase & ignoreWhitespace) {
            sameString = (expect.trim()).equals((actual.trim()));
        } else if (ignoreCase & ignoreWhitespace) {
            sameString = (expect.trim()).equalsIgnoreCase((actual.trim()));
        } else if (!ignoreCase & !ignoreWhitespace) {
            sameString = expect.equals(actual);
        }

        if (!sameString) {
            throw new TestException(
                    String.format("Verify failed for %s, expect %s, but found %s", message, expect, actual));
        }
    }

}
