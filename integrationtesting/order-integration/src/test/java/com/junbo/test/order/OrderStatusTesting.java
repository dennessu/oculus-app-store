/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.order.spec.model.Order;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import com.junbo.test.order.model.OrderEventInfo;
import com.junbo.test.order.model.enums.OrderStatus;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderStatusTesting extends BaseOrderTestClass {

    @Property(
            priority = Priority.BVT,
            features = "GET /Orders?userId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order status update - Paypal",
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
    public void testPaypalOrderStatus() throws Exception {
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String payPalId = testDataProvider.postPaymentInstrument(uid, payPalInfo);

        String orderId1 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        expectedOrderStatus.put(orderId1, OrderStatus.OPEN);

        String orderId2 = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, payPalId, false, offerList);

        Order order = Master.getInstance().getOrder(orderId2);
        order.setTentative(false);
        order.getPayments().get(0).setSuccessRedirectUrl("http://www.abc.com/");
        order.getPayments().get(0).setCancelRedirectUrl("http://www.abc.com/cancel/");
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
    public void testOrderHistory() throws Exception {
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

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
        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 1);

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

    @Property(
            priority = Priority.Dailies,
            features = "GET /Orders?userId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order status update - Price rating changed before payment charged",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user.",
                    "3. Post an order without setting tentative (OPEN)",
                    "4. Put offer with new offer revision(price changed)",
                    "5. Get order by user Id",
                    "6. Verify order status has been changed to PRICE_RATING_CHANGED",
                    "7. Try ro put order tentative to false",
                    "8. Verify response code is 409 with error msg",
                    "9. Put order",
                    "10. Get order by user Id",
                    "11. Verify order status has been changed to OPEN",
                    "12. Put order tentative to false",
                    "13. Get order by user Id",
                    "14. Verify order status is completed"
            }
    )
    @Test
    public void testPriceRatingChanged() throws Exception {
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_digital_normal1, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOfferPrice(offer_digital_normal1);
        Thread.sleep(2000);
        testDataProvider.getOrdersByUserId(uid);
        expectedOrderStatus.put(orderId, OrderStatus.PRICE_RATING_CHANGED);
        validationHelper.validateOrderStatus(expectedOrderStatus);

        testDataProvider.updateOrderTentative(orderId, false, 409);
        testDataProvider.getOrdersByUserId(uid);
        validationHelper.validateOrderStatus(expectedOrderStatus);

        testDataProvider.updateOrder(Master.getInstance().getOrder(orderId));

        expectedOrderStatus.clear();
        expectedOrderStatus.put(orderId, OrderStatus.OPEN);
        validationHelper.validateOrderStatus(expectedOrderStatus);

        testDataProvider.updateOrderTentative(orderId, false);
        testDataProvider.getOrdersByUserId(uid);

        expectedOrderStatus.clear();
        expectedOrderStatus.put(orderId, OrderStatus.COMPLETED);
        validationHelper.validateOrderStatus(expectedOrderStatus);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /order-events?orderId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test order events - checkout physical goods",
            steps = {
                    "1. Post a new user",
                    "2. Post a new credit card to user",
                    "3. Post an order without setting tentative",
                    "4. Get order events by order id",
                    "5. Verify action ='rate' , status ='completed'",
                    "6. Put order and set tentative to false",
                    "7. Get order events by order id",
                    "8. Verify action ='charge', status ='open'",
                    "8. Verify action ='charge', status ='completed'",
                    "9. Verify action ='fulfil', status ='open'",
                    "10. Post order event action ='fulfil', status ='completed'",
                    "11. Verify order status is completed"
            }
    )
    @Test
    public void testPhysicalGoodsOrderEvents() throws Exception {
        List<OrderEventInfo> expectedEventStatus = new ArrayList<>();
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_normal1, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, true, offerList);
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.RATE, EventStatus.COMPLETED));

        validationHelper.validateOrderEvents(orderId, expectedEventStatus);

        testDataProvider.updateOrderTentative(orderId, false);

        expectedEventStatus.add(new OrderEventInfo(OrderActionType.AUTHORIZE, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.AUTHORIZE, EventStatus.COMPLETED));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.FULFILL, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.FULFILL, EventStatus.PENDING));

        validationHelper.validateOrderEvents(orderId, expectedEventStatus);

        testDataProvider.getOrder(orderId);
        expectedOrderStatus.put(orderId,OrderStatus.PENDING_FULFILL);

        validationHelper.validateOrderStatus(expectedOrderStatus);

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.FULFILL, EventStatus.COMPLETED));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.CHARGE, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.CHARGE, EventStatus.COMPLETED));

        validationHelper.validateOrderEvents(orderId, expectedEventStatus);

        testDataProvider.getOrder(orderId);
        expectedEventStatus.clear();
        expectedOrderStatus.put(orderId,OrderStatus.COMPLETED);

        validationHelper.validateOrderStatus(expectedOrderStatus);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /order-events?orderId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test order events - checkout failure",
            steps = {
                    "1. Post a new user",
                    "2. Post a new credit card to user",
                    "3. Post an order without setting tentative",
                    "4. Modify payment external token",
                    "5. Put order and set tentative to false",
                    "6. Get order by order id",
                    "7. Verify order status = Failed",
                    "8. Get order events by order id",
                    "9. Verify action ='charge', status ='failed'",
            }
    )
    @Test
    public void testCheckoutFailure() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 2);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        testDataProvider.invalidateCreditCard(uid, creditCardId);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);
        //TODO Since credit card is unable to cause order failure
        //TODO we need checkout with paypal to cover these scenarios
    }


    @Property(
            priority = Priority.Dailies,
            features = "Post /order-events",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test order status - fulfilment failure",
            steps = {
                    "1. Post a new user",
                    "2. Post a new credit card to user",
                    "3. Post an order without setting tentative",
                    "4. Put order and set tentative to false",
                    "5. Post order events (action='fulfil', status='failed')",
                    "6. Get order by order id",
                    "7. Verify order status = Failed"
            }
    )
    @Test
    public void testFulfilmentFailure() throws Exception {
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();

        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_inApp_consumable2, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);
        testDataProvider.postOrderEvent(orderId, EventStatus.FAILED, OrderActionType.FULFILL);

        testDataProvider.getOrder(orderId);
        expectedOrderStatus.put(orderId, OrderStatus.FAILED);

        validationHelper.validateOrderStatus(expectedOrderStatus);
    }


    @Property(
            priority = Priority.Dailies,
            features = "GET /order-events?orderId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test order events - Refund",
            steps = {
                    "1. Post a new user",
                    "2. Post a new credit card to user",
                    "3. Post an order without setting tentative",
                    "4. Put order and set tentative to false",
                    "5. Put order with partial order items",
                    "6. Get order events by order Id",
                    "7. Verify order events (action='partial refund', status='open')",
                    "8. Verify order events (action='partial refund', status='completed')",
                    "9. Get order by order Id",
                    "10. Verify order status is partial refund",
                    "11. Put order with free order items",
                    "12. Verify order events (action='refund', status='open')",
                    "13. Verify order events (action='refund', status='completed')",
                    "14. Get order by order Id",
                    "15. Verify order status is refund"
            }
    )
    @Test
    public void testRefundOrderEvents() throws Exception {
        List<OrderEventInfo> expectedEventStatus = new ArrayList<>();
        Map<String, OrderStatus> expectedOrderStatus = new HashMap<>();
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        testDataProvider.refundPartialAmount(orderId, new BigDecimal(2));

        testDataProvider.getOrder(orderId);

        expectedEventStatus.add(new OrderEventInfo(OrderActionType.RATE, EventStatus.COMPLETED));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.CHARGE, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.CHARGE, EventStatus.COMPLETED));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.FULFILL, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.FULFILL, EventStatus.COMPLETED));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.REFUND, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.REFUND, EventStatus.COMPLETED));

        expectedOrderStatus.put(orderId, OrderStatus.REFUNDED);

        validationHelper.validateOrderEvents(orderId, expectedEventStatus);
        validationHelper.validateOrderStatus(expectedOrderStatus);

        testDataProvider.refundTotalQuantity(orderId);
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.REFUND, EventStatus.OPEN));
        expectedEventStatus.add(new OrderEventInfo(OrderActionType.REFUND, EventStatus.COMPLETED));

        validationHelper.validateOrderEvents(orderId, expectedEventStatus);
        validationHelper.validateOrderStatus(expectedOrderStatus);

    }

}
