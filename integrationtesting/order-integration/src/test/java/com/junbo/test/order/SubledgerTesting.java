/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.property.*;
import org.testng.annotations.Test;

/**
 * Created by weiyu_000 on 5/26/14.
 */
public class SubledgerTesting extends BaseOrderTestClass {

    @Property(
            priority = Priority.BVT,
            features = "GET /Subledger",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            release = Release.June2014,
            description = "Test get subledger ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order (offer1) and complete it",
                    "4. Post another user",
                    "5. Post new credit card to user",
                    "6. Post an order (offer1) and complete it",
                    "7. Get subledger",
                    "8. Verify subledger response"
            }
    )
    @Test
    public void testGetSubledger() throws Exception {

    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /Subledger",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            release = Release.June2014,
            description = "Test get mixed order status subledger",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "4. Post an order (digital offer) without setting tentative",
                    "5. Post an order (physical offer) and set tentative to false",
                    "6. Post another user",
                    "7. Post an order (digital offer) with paypal",
                    "8. Put order tentative and set redirect url",
                    "9. Post ewallet to user and credit enough balance",
                    "10. Post an order (physical offer) and set tentative to false",
                    "11. Verify subledger response"
            }
    )
    @Test
    public void testMixedOrderStatusSubledger() throws Exception {}

    @Property(
            priority = Priority.Dailies,
            features = "GET /Subledger",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            release = Release.June2014,
            description = "Test get subledger ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order (offer1) and complete it",
                    "4. Post another user",
                    "5. Post new credit card to user",
                    "6. Post an order (offer1) and complete it",
                    "7. Refund order1 ",
                    "8. Get subledger",
                    "9. Verify subledger response"
            }
    )
    @Test
    public void testRefundSubledger() throws Exception {

    }



}
