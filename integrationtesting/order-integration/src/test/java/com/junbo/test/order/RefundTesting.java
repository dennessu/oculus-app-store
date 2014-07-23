/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.billing.entities.TransactionInfo;
import com.junbo.test.billing.enums.TransactionStatus;
import com.junbo.test.billing.enums.TransactionType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.property.*;
import com.junbo.test.order.model.OrderInfo;
import com.junbo.test.order.model.enums.OrderStatus;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;

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
                    "4. Refund order 1 quantity and refund order 2 total amount",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info",
                    "7. Get order by order Id",
                    "8. Verify order response"
            }
    )
    @Test
    public void testOrderFullRefund() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_inApp_consumable1, 2);
        offerList.put(offer_digital_normal2, 1);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, country, currency, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        OrderInfo expectedOrderInfo = testDataProvider.getExpectedOrderInfo(uid, country, currency,
                "en_US", false, OrderStatus.COMPLETED, creditCardId, orderId, offerList);

        Map<String, Integer> refundOfferList = new HashedMap();
        refundOfferList.put(offer_inApp_consumable1, 2);

        Map<String, BigDecimal> partialRefundAmounts = new HashedMap();
        partialRefundAmounts.put(offer_digital_normal2, new BigDecimal(10));

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, refundOfferList, partialRefundAmounts);

        testDataProvider.refundOrder(orderId, refundOfferList, partialRefundAmounts);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(1);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(expectedOrderInfo.getTotalAmount().add(expectedOrderInfo.getTotalTax()));
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

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
                    "8. Verify order response info",
                    "9. Put order with partial amount",
                    "10. Get balance by order Id",
                    "11. Verify transactions contain expected refund info,",
                    "12. Get order by order Id",
                    "13. Verify order response info"
            }
    )
    @Test
    public void testRepeatedOrderPartialAmountRefund() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_digital_normal1, 1);
        offerList.put(offer_digital_normal2, 1);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        OrderInfo expectedOrderInfo = testDataProvider.getExpectedOrderInfo(uid, country, currency,
                "en_US", false, OrderStatus.COMPLETED, creditCardId, orderId, offerList);


        Map<String, BigDecimal> partialRefundAmounts = new HashedMap();
        partialRefundAmounts.put(offer_digital_normal1, new BigDecimal(5));
        partialRefundAmounts.put(offer_digital_normal2, new BigDecimal(5));

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, null, partialRefundAmounts);

        testDataProvider.refundOrder(orderId, null, partialRefundAmounts);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(1);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(expectedOrderInfo.getTotalAmount().add(expectedOrderInfo.getTotalTax()));
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, null, partialRefundAmounts);

        testDataProvider.refundOrder(orderId, null, partialRefundAmounts);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        balanceId = testDataProvider.getBalancesByOrderId(orderId).get(2);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);
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
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_inApp_consumable1, 3);
        offerList.put(offer_inApp_consumable2, 3);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        OrderInfo expectedOrderInfo = testDataProvider.getExpectedOrderInfo(uid, country, currency,
                "en_US", false, OrderStatus.COMPLETED, creditCardId, orderId, offerList);

        Map<String, Integer> refundOfferList = new HashedMap();
        refundOfferList.put(offer_inApp_consumable1, 1);
        refundOfferList.put(offer_inApp_consumable2, 2);

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, refundOfferList, null);

        testDataProvider.refundOrder(orderId, refundOfferList, null);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(1);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(expectedOrderInfo.getTotalAmount().add(expectedOrderInfo.getTotalTax()));
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            release = Release.June2014,
            description = "Test order refund - partial item refund ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user",
                    "3. Post an order and complete it",
                    "4. Put order with partial item(refund the first item)",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info,",
                    "7. Get order by order Id",
                    "8. Verify order response info"
            }
    )
    @Test
    public void testOrderPartialItemRefund() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_inApp_consumable1, 2);
        offerList.put(offer_digital_normal2, 1);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        OrderInfo expectedOrderInfo = testDataProvider.getExpectedOrderInfo(uid, country, currency,
                "en_US", false, OrderStatus.COMPLETED, creditCardId, orderId, offerList);

        Map<String, Integer> refundOfferList = new HashedMap();
        refundOfferList.put(offer_inApp_consumable1, 2);

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, refundOfferList, null);

        testDataProvider.refundOrder(orderId, refundOfferList, null);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(1);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(expectedOrderInfo.getTotalAmount().add(expectedOrderInfo.getTotalTax()));
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

    }


    @Property(
            priority = Priority.BVT,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test refund pre-order offer",
            steps = {
                    "1. Post a new user",
                    "2. Post credit card to user",
                    "3. Post an order (pre-order offer)",
                    "4. Modify system runtime to current time",
                    "5. Post order events(fulfil completed)",
                    "6. Reset system runtime back",
                    "7. Put order without order items",
                    "8. Verify orders response",
                    "9. Get order events by order Id",
                    "10. Verify order events response",
                    "11. Get balance by order Id",
                    "12. Verify transactions contain expected refund info,"
            }
    )
    @Test
    public void testRefundDigitalPreOrder() throws Exception {
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;
    }

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
                    "4. Refund order 1 quantity and refund order 2 total amount",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info",
                    "7. Get order by order Id",
                    "8. Verify order response"
            }
    )
    @Test
    public void testRefundPhysical() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_physical_normal1, 2);
        offerList.put(offer_physical_normal2, 1);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, country, currency, creditCardId, true, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        OrderInfo expectedOrderInfo = testDataProvider.getExpectedOrderInfo(uid, country, currency,
                "en_US", true, OrderStatus.COMPLETED, creditCardId, orderId, offerList);

        Map<String, Integer> refundOfferList = new HashedMap();
        refundOfferList.put(offer_physical_normal1, 1);

        Map<String, BigDecimal> partialRefundAmounts = new HashedMap();
        partialRefundAmounts.put(offer_physical_normal2, new BigDecimal(2));

        testDataProvider.getRefundedOrderInfo(expectedOrderInfo, refundOfferList, partialRefundAmounts);

        testDataProvider.refundOrder(orderId, refundOfferList, partialRefundAmounts);

        validationHelper.validateOrderInfo(orderId, expectedOrderInfo);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(1);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(expectedOrderInfo.getTotalAmount().add(expectedOrderInfo.getTotalTax()));
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

    }


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
                    "4. Refund free digital",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info",
                    "7. Get order by order Id",
                    "8. Verify order response"
            }
    )
    @Test
    public void testRefundFreeDigital() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_digital_free, 1);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, country, currency, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        Map<String, Integer> refundOfferList = new HashedMap();
        refundOfferList.put(offer_digital_free, 1);

        testDataProvider.refundOrder(orderId, refundOfferList, null);

    }

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
                    "4. Refund free phycisal",
                    "5. Get balance by order Id",
                    "6. Verify transactions contain expected refund info",
                    "7. Get order by order Id",
                    "8. Verify order response"
            }
    )
    @Test
    public void testRefundFreePhysical() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_physical_free, 1);

        String uid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, country, currency, creditCardId, true, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        Map<String, BigDecimal> partialRefundAmounts = new HashedMap();
        partialRefundAmounts.put(offer_physical_free, new BigDecimal(0));

        testDataProvider.refundOrder(orderId, null, partialRefundAmounts);

    }


    @Property(
            priority = Priority.Dailies,
            features = "GET /Orders?userId={key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Manual,
            description = "Test order refund - Price rating changed ",
            steps = {
                    "1. Post a new user",
                    "2. Post new credit card to user.",
                    "3. Post an order and set tentative to false",
                    "4. Put offer with new offer revision(price changed)",
                    "5. Get order by user Id",
                    "7. Partial refund order item amount",
                    "8. Get order by user Id",
                    "9. Verify order response",
            }
    )
    @Test
    public void testRefundPriceRatingChanged() throws Exception {
        String uid = testDataProvider.createUser();

        Map<String, Integer> offerList = new HashedMap();
        offerList.put(offer_digital_normal1, 1);

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        testDataProvider.updateOfferPrice(offer_digital_normal1);
        Thread.sleep(2000);
        testDataProvider.getOrdersByUserId(uid);

        Map<String, BigDecimal> partialRefundAmounts = new HashedMap();
        partialRefundAmounts.put(offer_digital_normal1, new BigDecimal(10));
        testDataProvider.refundOrder(orderId, null, partialRefundAmounts);

        testDataProvider.getOrdersByUserId(uid);
    }

}
