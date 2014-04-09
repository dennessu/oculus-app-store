/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Locale.
 */
public class Locale {
    public static final String DEFAULT = "DEFAULT";
    public static final List<String> LOCALES = Collections.unmodifiableList(Arrays.asList(
            DEFAULT,
            "en_US",
            "zh_CN"
    ));

    private Locale(){}
}
