/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.def;

/**
 * Consts of status reason.
 */
public class EntitlementStatusReason {
    private EntitlementStatusReason() {
    }

    public static final String NOT_START = "NOT_START";
    public static final String EXPIRED = "EXPIRED";
    public static final String TRANSFERRED = "TRANSFERRED";
    public static final String DELETED = "DELETED";
}
