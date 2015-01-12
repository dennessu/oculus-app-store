/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Input devices.
 */
public enum InputDevices {
    KEYBOARD, MOUSE, CONTROLLER, TOUCHPAD, GAMEPAD, HEAD_LOOK;

    public static final List<InputDevices> ALL = Arrays.asList(InputDevices.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }

    public static boolean contains(String type) {
        if (type == null) {
            return false;
        }
        try {
            InputDevices.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
