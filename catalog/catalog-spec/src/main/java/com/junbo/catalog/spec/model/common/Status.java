/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity status.
 */
public final class Status {
    public static final String DRAFT = "DRAFT";
    public static final String PENDING_REVIEW = "PENDING_REVIEW";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    public static final Set<String> ALL_STATUSES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(DRAFT, PENDING_REVIEW, APPROVED, REJECTED)));

    private Status(){ }
}
