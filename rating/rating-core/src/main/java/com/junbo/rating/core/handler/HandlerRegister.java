/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.Criterion;
import com.junbo.rating.core.context.RatingContext;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lizwu on 2/21/14.
 */
public final class HandlerRegister {
    private static ConcurrentHashMap<String, CriterionHandler> register;

    public void setRegister(ConcurrentHashMap<String, CriterionHandler> register) {
        this.register = register;
    }

    public static boolean isSatisfied(Criterion criterion, RatingContext context) {
        Assert.notNull(criterion, "criterion");
        Assert.notNull(criterion.getPredicate(), "predicate");

        CriterionHandler handler = register.get(criterion.getPredicate().toString());

        if (handler == null) {
            throw new IllegalStateException(criterion.getPredicate() + " handler is not registered.");
        }

        return handler.validate(criterion, context);
    }
}
