/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * EntitlementType enum.
 */
public enum EntitlementType {
    DOWNLOAD, RUN, DEVELOPER, ALLOW_IN_APP;

    public static final List<EntitlementType> ALL = Arrays.asList(EntitlementType.values());

    public boolean is(String type) {
        return this.name().equals(type);
    }
}
