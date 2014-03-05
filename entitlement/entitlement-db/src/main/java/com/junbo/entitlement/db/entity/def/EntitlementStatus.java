/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity.def;

import java.util.EnumSet;

/**
 * EntitlementStatus enum.
 */
public enum EntitlementStatus {
    ACTIVE, DISABLED, PENDING, DELETED, BANNED, MANAGED;

    public static final EnumSet<EntitlementStatus> LIFECYCLE_NOT_MANAGED_STATUS = EnumSet.of(DELETED, BANNED);
    public static final EnumSet<EntitlementStatus> NOT_TRANSFERABLE = EnumSet.of(DELETED, BANNED, DISABLED);
}
