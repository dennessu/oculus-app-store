/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion.criterion;

import com.junbo.common.jackson.annotation.OfferAttributeId;

import java.util.List;

/**
 * Category Scope criterion.
 */
public class CategoryScopeCriterion extends Criterion{
    @OfferAttributeId
    private List<String> categories;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
