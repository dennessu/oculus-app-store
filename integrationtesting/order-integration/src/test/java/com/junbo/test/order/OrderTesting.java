/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.order.spec.model.Order;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.enums.OrderStatus;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderTesting extends BaseOrderTestClass {
    private LogHelper logHelper = new LogHelper(OrderTesting.class);
    private Country country = Country.DEFAULT;
    private Currency currency = Currency.DEFAULT;

    @Property(
            priority = Priority.BVT,
            features = "GET /Orders?userId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order status - order history ",
            steps = {
                    "1. Post a new user",
                    "2. Post new paypal to user",
                    "3. Post an order without setting tentative (OPEN)",
                    "4. Put order tentative and set redirect url (PENDING_CHARGE)",
                    "5. Get orders by user id",
                    "6. Verify orders response"
            }
    )
    @Test
    public void testPaypalOrderStatusUpdate() throws Exception {
        Map<String,OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String payPalId = testDataProvider.postPaymentInstrument(uid, payPalInfo);

        String orderId1 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        expectedOrderStatus.put(orderId1,OrderStatus.OPEN);

        String orderId2 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId2);
        order.setTentative(false);
        order.setSuccessRedirectUrl("http://www.abc.com/");
        order.setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId2 = testDataProvider.updateOrder(order);

        expectedOrderStatus.put(orderId2,OrderStatus.PENDING_CHARGE);

        validationHelper.validateOrderStatus(expectedOrderStatus);
    }

    @Property(
            priority = Priority.BVT,
            features = "GET /Orders?userId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order status - order history ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user.",
                    "3. Post an order without setting tentative (OPEN)",
                    "4. Post another order and set tentative to false (COMPLETED)",
                    "5. Post ewallet to user",
                    "6. Credit insufficient balance",
                    "7. Post an order and set tentative to false (FAILED)",
                    "8. Credit enough balance ",
                    "9. Post an order with physical good and set tentative to false (PENDING FULFIL)",
                    "10. Get orders by user id",
                    "11. Verify orders response"
            }
    )
    @Test
    public void testOrderStatus() throws Exception {
        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId_Open = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        String orderId_Completed = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId_Completed,false);

        //TODO

    }




}
