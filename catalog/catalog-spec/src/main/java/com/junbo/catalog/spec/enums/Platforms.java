/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Action type.
 */
public enum Platforms {
    PC, MAC, LINUX, ANDROID;

    public static final List<Platforms> ALL = Arrays.asList(Platforms.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        try {
            Platforms.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
