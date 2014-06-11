/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.property.*;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by weiyu_000 on 6/11/14.
 */
public class RefundTesting extends BaseOrderTestClass {

    @Property(
            priority = Priority.BVT,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            release = Release.June2014,
            description = "Test order refund - full refund",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order and complete it",
                    "4. Put order without order items",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info",
                    "7. Get order by order Id",
                    "8. Verify order response"
            }
    )
    @Test
    public void testOrderFullRefund() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);

        String uid1 = testDataProvider.createUser();
        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId1 = testDataProvider.postPaymentInstrument(uid1, creditCardInfo1);

        String order = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);

        testDataProvider.updateOrderTentative(order, false);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            release = Release.June2014,
            description = "Test order refund - partial amount refund ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order and complete it",
                    "4. Put order with partial amount",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info,",
                    "7. Get order by order Id",
                    "8. Verify order response info"
            }
    )
    @Test
    public void testOrderPartialAmountRefund() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId1 = testDataProvider.postPaymentInstrument(uid, creditCardInfo1);

        String order = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);

        testDataProvider.updateOrderTentative(order, false);
    }


    @Property(
            priority = Priority.Dailies,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            release = Release.June2014,
            description = "Test order refund - partial quantity refund ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order and complete it",
                    "4. Put order with partial item quantity",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info,",
                    "7. Get order by order Id",
                    "8. Verify order response info"
            }
    )
    @Test
    public void testOrderPartialQuantityRefund() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String order = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(order, false);
    }


}
