/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.junbo.common.jackson.annotation.EntitlementDefinitionId;

import java.util.List;

/**
 * Entitlement criterion.
 */
public class EntitlementCriterion extends Criterion {
    @EntitlementDefinitionId
    private List<Long> entitlements;

    public List<Long> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(List<Long> entitlements) {
        this.entitlements = entitlements;
    }
}
