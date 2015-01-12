/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Entity status.
 */
public enum Status {
    // draft -> pending_review -> approved -> obsolete
    //    /\        |
    //    |         V
    //    \---- rejected

    DRAFT, PENDING_REVIEW, APPROVED, REJECTED, OBSOLETE;

    public static final List<Status> ALL = Arrays.asList(Status.values());

    public boolean is(String status) {
        return this.name().equals(status);
    }

    public static boolean contains(String status) {
        if (status == null) {
            return false;
        }
        try {
            Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
