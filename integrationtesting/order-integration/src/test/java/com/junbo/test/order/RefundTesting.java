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
import org.testng.annotations.Test;

import java.math.BigDecimal;
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

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        BigDecimal refundTotalAmount = testDataProvider.refundTotalQuantity(orderId);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(refundTotalAmount);
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

        //TODO verify order response
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
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        BigDecimal refundTotalAmount = testDataProvider.refundPartialAmount(orderId, new BigDecimal(4.5));

        String balanceId = testDataProvider.getBalancesByOrderId(orderId);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(refundTotalAmount);
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

        //TODO verify order response
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

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, 4, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        BigDecimal refundTotalAmount = testDataProvider.refundPartialQuantity(orderId, 2);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(refundTotalAmount);
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

        //TODO verify order response
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
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);

        String uid = testDataProvider.createUser();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String creditCardId = testDataProvider.postPaymentInstrument(uid, creditCardInfo);

        String orderId = testDataProvider.postOrder(
                uid, Country.DEFAULT, Currency.DEFAULT, creditCardId, false, 4, offerList);

        testDataProvider.updateOrderTentative(orderId, false);

        BigDecimal refundTotalAmount = testDataProvider.refundPartialItem(orderId);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId);

        TransactionInfo transactionInfo = new TransactionInfo();
        transactionInfo.setPaymentInstrumentId(creditCardId);
        transactionInfo.setAmount(refundTotalAmount);
        transactionInfo.setCurrency(Currency.DEFAULT);
        transactionInfo.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionInfo.setTransactionType(TransactionType.REFUND);

        validationHelper.validateSingleTransaction(balanceId, transactionInfo);

        //TODO verify order response
    }


    @Property(
            priority = Priority.BVT,
            features = "Put /orders/{key}",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
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
    public void testRefundPreOrder() throws Exception {
    }




}
