/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.SystemRuntimeHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created by weiyu_000 on 6/16/14.
 */
public class PreOrderTesting extends BaseOrderTestClass  {

    @Property(
            priority = Priority.BVT,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test pre-order checkout flow",
            steps = {
                    "1. Post a new user",
                    "2. Post credit card to user",
                    "3. Post an order (pre-order offer)",
                    "4. Get order by order Id",
                    "5. Verify order response",
                    "6. Get balances by order Id",
                    "7. Verify balances response",
                    "8. Modify system runtime to current time",
                    "9. Post order events(fulfil completed)",
                    "10. Reset system runtime back",
                    "11. Get order by order Id",
                    "12. Verify orders response",
                    "13. Get order events by order Id",
                    "14. Verify order events response"
            }
    )
    @Test
    public void testPreOrderCheckout() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_preOrder, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.set(Calendar.YEAR, 2020);
        Date releaseDate = calendar.getTime();
        SystemRuntimeHelper.systemRuntime(releaseDate);

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test pre-order checkout flow",
            steps = {
                    "1. Post a new user",
                    "2. Post ewallet to user and charge 5 dollars",
                    "3. Post an order (pre-order offer)",
                    "4. Modify system runtime to current time",
                    "5. Post order events(fulfil completed)",
                    "6. Verify order response error",
                    "7. Reset system runtime back"

            }
    )
    @Test
    public void testInsufficientPreOrder() throws Exception {

    }

    @Property(
            priority = Priority.Dailies,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test pre-order checkout flow",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order (pre-order offer)",
                    "4. Post order events(fulfil completed)",
                    "5. Verify order response error",
            }
    )
    @Test
    public void testAdvancePreOrder() throws Exception {

    }





}
