/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity.def;

import com.junbo.common.util.Identifiable;

/**
 * EntitlementType enum.
 */
public enum EntitlementType implements Identifiable<Integer> {
    ONLINE_ACCESS(1), SUBSCRIPTIONS(2), DOWNLOAD(3), DEFAULT(0);

    private Integer id;

    EntitlementType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
