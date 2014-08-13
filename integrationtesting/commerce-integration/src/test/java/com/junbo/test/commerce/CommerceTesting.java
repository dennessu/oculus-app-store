/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.commerce;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by weiyu_000 on 8/13/14.
 */
public class CommerceTesting  extends TestCaseBase{

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            environment = "release",
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout free offer",
            steps = {
                    "1. Get user id from access token info",
                    "2. Post new credit card to new user.",
                    "3. Post order to checkout",
                    "4. Update order tentative to false",
                    "5. Get the entitlements by uid",
                    "6. Verify the entitlements are active",
            }
    )
    @Test
    public void testCheckoutFreeDigital() throws Exception {
       String uid = null;

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_free, 1);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, false, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

    }

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Order,
            environment = "release",
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout free offer",
            steps = {
                    "1. Get user id from access token info",
                    "2. Post new credit card to new user.",
                    "3. Post order to checkout",
                    "4. Verify the order response info",
                    "5. Update order tentative to false",
            }
    )
    @Test
    public void testCheckoutFreePhysical() throws Exception {
        String uid = null;

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_free, 1);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, true, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

    }


}
