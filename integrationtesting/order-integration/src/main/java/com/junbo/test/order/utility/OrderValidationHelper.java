/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.order.utility;

import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.model.Results;
import com.junbo.order.spec.model.Order;
import com.junbo.order.spec.model.OrderEvent;
import com.junbo.test.billing.entities.TransactionInfo;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.order.model.OrderEventInfo;
import com.junbo.test.order.model.enums.OrderStatus;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;

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

    public void validateOrderEvents(String orderId, List<OrderEventInfo> expectedOrderEvents)
            throws Exception {
        Results<OrderEvent> orderEventResults = testDataProvider.getOrderEventsByOrderId(orderId);
        List<OrderEvent> orderEvents = orderEventResults.getItems();

        verifyEqual(orderEvents.size(), expectedOrderEvents.size(), "verify order events size");

        for (int i = 0; i < expectedOrderEvents.size(); i++) {
            verifyEqual(orderEvents.get(i).getAction(), expectedOrderEvents.get(i).toString(),
                    "verify order action type");
            verifyEqual(orderEvents.get(i).getStatus(), expectedOrderEvents.get(i).toString(), "verify event status");
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

}
