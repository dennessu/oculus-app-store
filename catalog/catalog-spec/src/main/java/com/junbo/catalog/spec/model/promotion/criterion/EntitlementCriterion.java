/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion.criterion;

import com.junbo.common.jackson.annotation.ItemId;

import java.util.List;

/**
 * Entitlement criterion.
 */
public class EntitlementCriterion extends Criterion {
    @ItemId
    private List<String> items;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }
}
