/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import java.util.Date;

/**
 * Context utilities.
 */
public class ContextUtils {
    private ContextUtils() { }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getCurrentUser() {
        // TODO
        return "TODO-User";
    }

    public static String getCurrentClient() {
        return "TODO-Client";
    }
}
