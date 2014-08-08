/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.order.model.enums.EventStatus;
import com.junbo.test.order.model.enums.OrderActionType;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by weiyu_000 on 6/16/14.
 */
public class PreOrderTesting extends BaseOrderTestClass {

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

        offerList.put(offer_physical_preOrder, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, true, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        /*
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.set(Calendar.YEAR, 2020);
        Date releaseDate = calendar.getTime();
        SystemRuntimeHelper.systemRuntime(releaseDate);
        */

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);


    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Disable,
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

    @Property(
            priority = Priority.BVT,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test pre-order checkout flow",
            steps = {
                    "1. Post a new user",
                    "2. Create paypal account to user",
                    "3. Post an order(preorder)",
                    "4. Put order tentative to false",
                    "5. Complete paypal charge",
                    "6. Verify order status is preordered",
                    "7. Post order event fulfil completed",
                    "8. Verify order status is pending"
            }
    )
    @Test
    public void testPreOrderCheckoutByPaypal() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_preOrder, 1);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(Country.DEFAULT);
        String paymentId = testDataProvider.postPaymentInstrument(uid, payPalInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT,  paymentId, true, offerList);

        String token = testDataProvider.getPaypalToken(orderId);
        testDataProvider.emulatePaypalCallback(orderId,token);

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);

        //TODO Validation

    }

    @Property(
            priority = Priority.BVT,
            features = "Pre-order scenarios",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test pre-order checkout flow",
            steps = {
                    "1. Post a new user",
                    "2. Create ewallet account to user",
                    "3. Credit enough stored value balance",
                    "4. Post an order(preorder)",
                    "5. Put order tentative to false",
                    "6. Verify order status is preordered",
                    "7. Post order event fulfil completed",
                    "8. Verify order status is pending"
            }
    )
    @Test
    public void testPreOrderCheckoutByEwallet() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_preOrder, 1);

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        String paymentId = testDataProvider.postPaymentInstrument(uid, ewalletInfo);

        testDataProvider.creditWallet(uid, new BigDecimal(50));

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, paymentId, true, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        testDataProvider.postOrderEvent(orderId, EventStatus.COMPLETED, OrderActionType.FULFILL);

        //TODO Validation
    }



}
