/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.order.spec.model.Order;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.*;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

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
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

        String uid1 = testDataProvider.createUser();
        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId1 = testDataProvider.postPaymentInstrument(uid1, creditCardInfo1);

        String order1 = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);

        testDataProvider.updateOrderTentative(order1, false);

        CreditCardInfo creditCardInfo2 = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId2 = testDataProvider.postPaymentInstrument(uid1, creditCardInfo2);

        String order2 = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId2, false, offerList);

        testDataProvider.updateOrderTentative(order2, false);

        testDataProvider.getSubledger(offer_digital_normal1);

        //TODO verify subledger

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
    public void testMixedOrderStatusSubledger() throws Exception {
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

        String uid1 = testDataProvider.createUser();
        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId1 = testDataProvider.postPaymentInstrument(uid1, creditCardInfo1);

        String order1 = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);

        offerList.clear();
        offerList.put(offer_physical_normal1, 1);
        String order2 = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);
        testDataProvider.updateOrderTentative(order2, false);

        String uid2 = testDataProvider.createUser();
        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String payPalId = testDataProvider.postPaymentInstrument(uid2, payPalInfo);

        offerList.clear();
        offerList.put(offer_digital_normal1, 1);
        String orderId3 = testDataProvider.postOrder(
                uid2, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId3);
        order.setTentative(false);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.abc.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId3 = testDataProvider.updateOrder(order);

        offerList.clear();
        offerList.put(offer_physical_normal1, 1);
        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid2, ewalletInfo);
        testDataProvider.creditWallet(uid2, new BigDecimal(100));

        String order4 = testDataProvider.postOrder(
                uid2, Country.DEFAULT, Currency.DEFAULT, ewalletId, true, offerList);


        testDataProvider.updateOrderTentative(order4, false);

        testDataProvider.getSubledger(offer_digital_normal1);

        //TODO verify subledger

    }

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
                    "7. Refund first order and partial refund second order",
                    "8. Get subledger",
                    "9. Verify subledger response"
            }
    )
    @Test
    public void testRefundSubledger() throws Exception {

        //TODO Waitting refund online
    }

}
