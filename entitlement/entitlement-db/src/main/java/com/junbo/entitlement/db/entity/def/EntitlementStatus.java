/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity.def;

import com.junbo.common.util.Identifiable;

import java.util.EnumSet;

/**
 * EntitlementStatus enum.
 */
public enum EntitlementStatus implements Identifiable<Integer> {
    ACTIVE(0), DISABLED(2), PENDING(3), DELETED(-1), BANNED(-2), MANAGED(1);

    private Integer id;

    EntitlementStatus(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public static final EnumSet<EntitlementStatus> LIFECYCLE_NOT_MANAGED_STATUS = EnumSet.of(DELETED, BANNED);
    public static final EnumSet<EntitlementStatus> NOT_TRANSFERABLE = EnumSet.of(DELETED, BANNED, DISABLED);
}
