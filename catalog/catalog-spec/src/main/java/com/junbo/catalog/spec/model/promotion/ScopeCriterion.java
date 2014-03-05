/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import java.util.List;

/**
 * Scope criterion.
 */
public class ScopeCriterion extends Criterion {
    private List<Long> entities;

    public List<Long> getEntities() {
        return entities;
    }

    public void setEntities(List<Long> entities) {
        this.entities = entities;
    }
}
