/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.def;

import com.junbo.common.util.Identifiable;

/**
 * EntitlementType enum.
 */
public enum EntitlementType implements Identifiable<Integer> {
    DEFAULT(0), DEVELOPER(1), DOWNLOAD(2), ONLINE_ACCESS(3), IAP(4), SUBSCRIPTIONS(4);

    private Integer id;

    EntitlementType(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }
}
