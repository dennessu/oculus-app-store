/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.criterion.Predicate;
import com.junbo.rating.core.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by lizwu on 2/21/14.
 */
public class HandlerRegisterTest extends BaseTest{
    @Test
    public void test() {
        HandlerRegister.getHandler(Predicate.INCLUDE_OFFER.toString());
        HandlerRegister.getHandler(Predicate.EXCLUDE_OFFER.toString());
        HandlerRegister.getHandler(Predicate.INCLUDE_CATEGORY.toString());
        HandlerRegister.getHandler(Predicate.EXCLUDE_CATEGORY.toString());
        HandlerRegister.getHandler(Predicate.INCLUDE_ENTITLEMENT.toString());
        HandlerRegister.getHandler(Predicate.EXCLUDE_ENTITLEMENT.toString());
        HandlerRegister.getHandler(Predicate.ORDER_ABSOLUTE_VALUE_ABOVE.toString());
        HandlerRegister.getHandler(Predicate.ORDER_ITEM_COUNT_ABOVE.toString());

        try {
            HandlerRegister.getHandler("ILLEGAL_TYPE");
            Assert.fail();
        } catch (IllegalStateException e) {

        }
    }
}
