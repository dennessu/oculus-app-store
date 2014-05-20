/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion.criterion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Criterion.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="criterionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OfferScopeCriterion.class, name = "OfferScopeCriterion"),
        @JsonSubTypes.Type(value = CategoryScopeCriterion.class, name = "CategoryScopeCriterion"),
        @JsonSubTypes.Type(value = EntitlementCriterion.class, name = "EntitlementCriterion"),
        @JsonSubTypes.Type(value = CouponCriterion.class, name = "CouponCriterion"),
        @JsonSubTypes.Type(value = OrderCriterion.class, name = "OrderCriterion")
})
public abstract class Criterion {
    private Predicate predicate;

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }
}
