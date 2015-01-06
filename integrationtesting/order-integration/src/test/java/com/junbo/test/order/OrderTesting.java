/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Created by weiyu_000 on 7/17/14.
 */
public class OrderTesting extends BaseOrderTestClass {
    @Property(
            priority = Priority.BVT,
            features = "Post /orders",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test Post Order without offer quantity",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post order without order quantity",
                    "6. Verify orders response"
            }
    )
    @Test
    public void testPostOrderWithoutQuantity() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 0);
        offerList.put(offer_digital_normal2, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList, 400);

        assert Master.getInstance().getApiErrorMsg().contains("Field value is invalid");
    }

    @Property(
            priority = Priority.BVT,
            features = "Post /orders",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test Post Order with rating error",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post order with XXX currency code and non-free offer",
                    "4. Verify the rating error returns and its details are included"
            }
    )
    @Test
    public void testPostOrderRatingError() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_digital_normal1, 1);

        testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.FREE, null, false, offerList, 412);

        assert Master.getInstance().getApiErrorMsg().contains("price is not configured in offer.");
        assert Master.getInstance().getApiErrorMsg().contains("133.147");
    }

    @Property(
            priority = Priority.BVT,
            features = "Post /orders",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test post order with invalid offer id",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post order with invalid offer id",
                    "4. Verify orders response"
            }
    )
    @Test
    public void testPostOrderWithInvalidOfferId() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put("Invalid", 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList, 412);

        assert Master.getInstance().getApiErrorMsg().contains("Offer Not Found");
    }

    @Property(
            priority = Priority.BVT,
            features = "Post /orders",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test post order with invalid payment-instrument",
            steps = {
                    "1. Post a new user",
                    "2. Post order without payment id",
                    "3. Verify orders response"
            }
    )
    @Test
    public void testPostOrderWithInvalidPayment() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

        testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, "Invalid", false, offerList, 412);

        //TODO verfiy response
    }

    @Property(
            priority = Priority.BVT,
            features = "Post /orders",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test post order without shipping address",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post physical order without shipping address",
                    "6. Verify orders response"
            }
    )
    @Test
    public void testPostOrderWithoutShippingAddress() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_normal1, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        //TODO verify response
    }

    @Property(
            priority = Priority.BVT,
            features = "Get /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            description = "Test get order by invalid order id",
            steps = {
                    "1. Post a new user",
                    "2. Get order by invalid order Id",
                    "3. Post physical order without shipping address",
                    "6. Verify orders response"
            }
    )
    @Test
    public void testGetOrderByInvalidOrderId() throws Exception {
        testDataProvider.getOrder("0", 404);
        //TODO verify response
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get /orders-events/",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get order events by null",
            steps = {
                    "1. Post a new user",
                    "2. Get order events by null id",
            }
    )
    @Test
    public void testGetOrderEvents() throws Exception {
        testDataProvider.getOrderEvents("", 400);

    }


}
