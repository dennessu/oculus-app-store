/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderTesting extends BaseOrderTestClass {
    private LogHelper logHelper = new LogHelper(OrderTesting.class);
    private Country country = Country.DEFAULT;
    private Currency currency = Currency.DEFAULT;

    @Property(
            priority = Priority.BVT,
            features = "OrderStatus",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order status - order history ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user.",
                    "3. Post an order without setting tentative",
                    "4. Post another order and set tentative to false",
                    "5. Post an order without setting tentative",

            }
    )
    @Test
    public void testDigitalGoodCheckoutByCreditCard() throws Exception {
        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);
    }

}
