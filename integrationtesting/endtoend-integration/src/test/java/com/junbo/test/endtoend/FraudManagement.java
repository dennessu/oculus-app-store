/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.endtoend;

import com.junbo.test.endtoend.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by Yunlong on 5/28/14.
 */
public class FraudManagement extends BaseTestClass {
    @Property(
            priority = Priority.BVT,
            features = "FraudManagement",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test fraud check counting by customer email",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to new user.",
                    "3. Checkout offer more than 3 times within 1 min",
                    "4. Verify Gateway Reject when 3 or more Transactions occur within 1 minutes"

            }
    )
    @Test
    public void testFraudCheckByEmail() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_digital_normal1, 1);

        String uid = testDataProvider.createUser("FraudTest", "FraudRejection_test@silkcloud.com");

        String creditCardId = testDataProvider.getCreditCard(uid);
        if (creditCardId == null || creditCardId.isEmpty()) {
            CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
            creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);
        }

        String order1 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(order1, false);

        String order2 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(order2, false);

        String order3 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(order3, false);

        String order4 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(order4, false);

    }

    @Property(
            priority = Priority.BVT,
            features = "FraudManagement",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test fraud check counting by unique Customer Id Per Credit Card Number",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to new user.",
                    "3. Checkout offer more than 3 times within 1 min",
                    "4. Verify Gateway Reject when 3 or more Transactions occur within 1 minutes"

            }
    )
    @Test
    public void testFraudCheckByCreditCardNum() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_digital_normal1, 1);

        String uid1 = testDataProvider.createUser();
        String uid2 = testDataProvider.createUser();
        String uid3 = testDataProvider.createUser();
        String uid4 = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId1 = testDataProvider.postPaymentInstrument(uid1, creditCardInfo);
        String creditCardId2 = testDataProvider.postPaymentInstrument(uid2, creditCardInfo);
        String creditCardId3 = testDataProvider.postPaymentInstrument(uid3, creditCardInfo);
        String creditCardId4 = testDataProvider.postPaymentInstrument(uid4, creditCardInfo);

        String order1 = testDataProvider.postOrder(
                uid1, Country.DEFAULT, Currency.DEFAULT, creditCardId1, false, offerList);

        testDataProvider.updateOrderTentative(order1, false);

        String order2 = testDataProvider.postOrder(
                uid2, Country.DEFAULT, Currency.DEFAULT, creditCardId2, false, offerList);

        testDataProvider.updateOrderTentative(order2, false);

        String order3 = testDataProvider.postOrder(
                uid3, Country.DEFAULT, Currency.DEFAULT, creditCardId3, false, offerList);

        testDataProvider.updateOrderTentative(order3, false);

        String order4 = testDataProvider.postOrder(
                uid4, Country.DEFAULT, Currency.DEFAULT, creditCardId4, false, offerList);

        testDataProvider.updateOrderTentative(order4, false);
    }

}
