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
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderTesting extends BaseOrderTestClass {

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
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String payPalId = testDataProvider.postPaymentInstrument(uid, payPalInfo);

        String orderId1 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        expectedOrderStatus.put(orderId1, OrderStatus.OPEN);

        String orderId2 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId2);
        order.setTentative(false);
        order.setSuccessRedirectUrl("http://www.abc.com/");
        order.setCancelRedirectUrl("http://www.abc.com/cancel/");
        orderId2 = testDataProvider.updateOrder(order);

        expectedOrderStatus.put(orderId2, OrderStatus.PENDING_CHARGE);

        Master.getInstance().initializeOrders();
        testDataProvider.getOrdersByUserId(uid);

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
                    "7. Post an order with physical good and set tentative to false (OPEN)",
                    "8. Credit enough balance ",
                    "9. Put order tentative again (PENDING FULFIL)",
                    "10. Get orders by user id",
                    "11. Verify orders response"
            }
    )
    @Test
    public void testOrderStatus() throws Exception {
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId_Open = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        expectedOrderStatus.put(orderId_Open, OrderStatus.OPEN);

        String orderId_Completed = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId_Completed, false);

        expectedOrderStatus.put(orderId_Completed, OrderStatus.COMPLETED);

        offerList.clear();
        offerList.add(offer_physical_normal1);
        offerList.add(offer_physical_normal2);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String ewalletId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);
        testDataProvider.creditWallet(uid, new BigDecimal(20));

        String order_Insufficient = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, ewalletId, true, offerList);

        testDataProvider.updateOrderTentative(order_Insufficient, false, 409);
        expectedOrderStatus.put(order_Insufficient, OrderStatus.OPEN);

        testDataProvider.creditWallet(uid, new BigDecimal(100));

        Master.getInstance().initializeOrders();
        testDataProvider.getOrdersByUserId(uid);
        validationHelper.validateOrderStatus(expectedOrderStatus);

        expectedOrderStatus.clear();

        String order_PendingFulfil = testDataProvider.updateOrderTentative(order_Insufficient, false, 200);
        expectedOrderStatus.put(order_PendingFulfil, OrderStatus.PENDING_FULFILL);

        Master.getInstance().initializeOrders();
        testDataProvider.getOrdersByUserId(uid);
        validationHelper.validateOrderStatus(expectedOrderStatus);
    }

}
