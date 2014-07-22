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
public enum InteractionModes {
    SINGLE_USER, MULTI_USER, CO_OP;

    public static final List<InteractionModes> ALL = Arrays.asList(InteractionModes.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        try {
            InteractionModes.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
