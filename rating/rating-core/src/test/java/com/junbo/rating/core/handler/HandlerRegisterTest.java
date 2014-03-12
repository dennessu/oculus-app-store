/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.Predicate;
import com.junbo.catalog.spec.model.promotion.ScopeCriterion;
import com.junbo.rating.core.BaseTest;
import com.junbo.rating.core.context.RatingContext;
import com.junbo.rating.spec.model.RatableItem;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by lizwu on 2/21/14.
 */
public class HandlerRegisterTest extends BaseTest{
    @Test
    public void test() {
        ScopeCriterion criterion = new ScopeCriterion();
        criterion.setPredicate(Predicate.INCLUDE_OFFER);
        Long itemId = generateId();
        criterion.setEntities(new ArrayList<Long>());
        criterion.getEntities().add(itemId);

        RatingContext context = new RatingContext();
        RatableItem item = new RatableItem();
        item.setOfferId(itemId);
        context.setCurrentItem(item);
        boolean result = HandlerRegister.isSatisfied(criterion, context);

        Assert.assertEquals(result, Boolean.TRUE.booleanValue());
    }
}
