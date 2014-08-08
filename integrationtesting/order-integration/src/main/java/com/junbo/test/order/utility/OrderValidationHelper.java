/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.model.Results;
import com.junbo.order.spec.model.*;
import com.junbo.test.billing.entities.TransactionInfo;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.order.model.*;
import com.junbo.test.order.model.enums.OrderStatus;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class OrderValidationHelper extends BaseValidationHelper {
    OrderTestDataProvider testDataProvider = new OrderTestDataProvider();

    public void validateOrderStatus(Map<String, OrderStatus> expectedOrderStatus) {
        Set<String> key = expectedOrderStatus.keySet();
        for (Iterator it = key.iterator(); it.hasNext(); ) {
            String orderId = (String) it.next();
            Order order = Master.getInstance().getOrder(orderId);
            verifyEqual(order.getStatus(), expectedOrderStatus.get(orderId).toString(), "verify orderStatus");
        }
    }

    public void validateEwalletBalance(String uid, BigDecimal expectedBalance) throws Exception{
        verifyEqual(testDataProvider.paymentProvider.getEwalletBalanceFromDB(uid), expectedBalance.setScale(2).toString(), "verify ewallet balance");
    }

    public void validateOrderEvents(String orderId, List<OrderEventInfo> expectedOrderEvents)
            throws Exception {
        Results<OrderEvent> orderEventResults = testDataProvider.getOrderEventsByOrderId(orderId);
        List<OrderEvent> orderEvents = orderEventResults.getItems();

        verifyEqual(orderEvents.size(), expectedOrderEvents.size(), "verify order events size");

        for (int i = 0; i < expectedOrderEvents.size(); i++) {
            verifyEqual(orderEvents.get(i).getAction().toString(), expectedOrderEvents.get(i).getOrderActionType().toString(),
                    "verify order action type");
            verifyEqual(orderEvents.get(i).getStatus(), expectedOrderEvents.get(i).getEventStatus().toString(), "verify event status");
        }
    }

    public void validateSingleTransaction(String balanceId, TransactionInfo expectedTransaction) {
        Balance balance = Master.getInstance().getBalance(balanceId);
        List<Transaction> transactions = balance.getTransactions();
        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(expectedTransaction.getTransactionType().toString()) &&
                    transaction.getStatus().equals(expectedTransaction.getTransactionStatus().toString())) {
                verifyEqual(IdConverter.idToHexString(transaction.getPiId()),
                        expectedTransaction.getPaymentInstrumentId(), "verify payment instrument id");
                verifyEqual(transaction.getAmount(), expectedTransaction.getAmount(), "verify transaction amount");
                return;
            }
        }
        throw new TestException("missing expected transaction in transaction list");
    }

    public void validateOrderInfo(String orderId, OrderInfo expectedOrderInfo) throws Exception {
        testDataProvider.getOrder(orderId);
        Order order = Master.getInstance().getOrder(orderId);
        verifyEqual(IdConverter.idToHexString(order.getUser()), expectedOrderInfo.getUserId(), "verify user id");
        verifyEqual(order.getStatus(), expectedOrderInfo.getOrderStatus().toString(), "verify order status");
        verifyEqual(order.getTentative(), expectedOrderInfo.isTentative(), "verify order tentative");
        verifyEqual(order.getCountry().getValue(), expectedOrderInfo.getCountry().toString(), "verify order country");
        verifyEqual(order.getCurrency().getValue(),
                expectedOrderInfo.getCurrency().toString(), "verify order currency");
        verifyEqual(order.getLocale().getValue(), expectedOrderInfo.getLocale(), "verify order locale");
        verifyEqual(order.getTotalAmount(), expectedOrderInfo.getTotalAmount(), "verify order total amount");
        verifyEqual(order.getTotalTax(), expectedOrderInfo.getTotalTax(), "verify order total tax");

        verifyEqual(order.getOrderItems().size(), expectedOrderInfo.getOrderItems().size(), "verify order items count");

        for (OrderItemInfo orderItemInfo : expectedOrderInfo.getOrderItems()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                if (IdConverter.idToHexString(orderItem.getOffer()).equals(orderItemInfo.getOfferId())) {
                    verifyEqual(orderItem.getQuantity(), orderItemInfo.getQuantity(), "verify order item quantity");
                    verifyEqual(orderItem.getUnitPrice(), orderItemInfo.getUnitPrice(), "verify order item unit price");
                    verifyEqual(orderItem.getTotalAmount(), orderItemInfo.getTotalAmount(), "verify order item amount");
                    verifyEqual(orderItem.getTotalTax(), orderItem.getTotalTax(), "verify order item tax");
                    break;
                }
            }
        }

        verifyEqual(IdConverter.idToHexString(order.getPayments().get(0).getPaymentInstrument()),
                expectedOrderInfo.getPaymentInfos().get(0).getPaymentId(), "verify payment id");

        verifyEqual(order.getBillingHistories().size(), expectedOrderInfo.getBillingHistories().size(),
                "verify billing history size");

        for (BillingHistoryInfo billingHistoryInfo : expectedOrderInfo.getBillingHistories()) {
            for (BillingHistory billingHistory : order.getBillingHistories()) {
                if (billingHistory.getBillingEvent().equals(billingHistoryInfo.getBillingAction().toString())) {
                    verifyEqual(billingHistory.getTotalAmount(), billingHistoryInfo.getTotalAmount(),
                            "verify billing history total amount");
                    verifyEqual(billingHistory.getPayments().size(), billingHistoryInfo.getPaymentInfos().size(),
                            "verify billing history payment size");
                    verifyEqual(billingHistory.getSuccess(), billingHistoryInfo.isSuccess(), "verify success or not");
                    for (PaymentInstrumentInfo paymentInstrumentInfo : billingHistoryInfo.getPaymentInfos()) {
                        for (BillingPaymentInfo billingPaymentInfo : billingHistory.getPayments()) {
                            if (IdConverter.idToHexString(billingPaymentInfo.getPaymentInstrument()).
                                    equals(paymentInstrumentInfo.getPaymentId())) {
                                verifyEqual(billingPaymentInfo.getPaymentAmount(),
                                        paymentInstrumentInfo.getPaymentAmount(),
                                        "verify billing history payment amount");
                            }
                        }
                    }

                    if (billingHistoryInfo.getRefundOrderItemInfos().size() != 0) {
                        verifyEqual(billingHistory.getRefundedOrderItems().size(),
                                billingHistoryInfo.getRefundOrderItemInfos().size(), "verify refunded items count");
                    }
                    else {
                        if(billingHistory.getRefundedOrderItems() != null){
                            throw new TestException("verify refunded items is null");
                        }
                    }

                    for (RefundOrderItemInfo refundOrderItemInfo : billingHistoryInfo.getRefundOrderItemInfos()) {
                        for (RefundOrderItem refundOrderItem : billingHistory.getRefundedOrderItems()) {
                            if (IdConverter.idToHexString(refundOrderItem.getOffer()).
                                    equals(refundOrderItemInfo.getOfferId())) {
                                verifyEqual(refundOrderItem.getQuantity(), refundOrderItemInfo.getQuantity(),
                                        "verify refund quantity");
                                verifyEqual(refundOrderItem.getRefundedAmount(), refundOrderItemInfo.getRefundAmount(),
                                        "verify refund amount");
                                verifyEqual(refundOrderItem.getRefundedTax(), refundOrderItemInfo.getRefundTax(),
                                        "verify refund tax");
                            }
                        }
                    }
                }
            }

        }

    }

}
