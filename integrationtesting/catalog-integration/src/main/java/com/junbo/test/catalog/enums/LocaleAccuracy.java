/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enum for locale accuracy.
 *
 * @author Jason
 */
public enum LocaleAccuracy {
    HIGH, MEDIUM, LOW;

    public static final List<LocaleAccuracy> ALL = Arrays.asList(LocaleAccuracy.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        try {
            LocaleAccuracy.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
