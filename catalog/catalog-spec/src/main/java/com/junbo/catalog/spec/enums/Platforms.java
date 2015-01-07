/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Platforms.
 */
public enum Platforms {
    PC("PC"), MAC("MAC"), LINUX("LINUX"), ANDROID("ANDROID"),
    VIDEO("VIDEO"), _360VIDEO("360VIDEO"), PHOTO("PHOTO"), _360PHOTO("360PHOTO");

    public static final List<Platforms> ALL = Arrays.asList(Platforms.values());

    private String value;

    private Platforms(String value) {
        this.value = value;
    }

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        if (type == null) {
            return false;
        }
        for (Platforms platform : ALL) {
            if (platform.value.equals(type)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
