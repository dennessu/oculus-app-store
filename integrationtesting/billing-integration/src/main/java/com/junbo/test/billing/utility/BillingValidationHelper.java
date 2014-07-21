/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing.utility;

import com.junbo.billing.spec.enums.TaxStatus;
import com.junbo.billing.spec.model.Balance;
import com.junbo.billing.spec.model.BalanceItem;
import com.junbo.billing.spec.model.TaxItem;
import com.junbo.billing.spec.model.Transaction;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.order.spec.model.Order;
import com.junbo.test.billing.enums.BalanceStatus;
import com.junbo.test.billing.enums.BalanceType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;

import java.math.BigDecimal;

/**
 * Created by Yunlong on 4/9/14.
 */
public class BillingValidationHelper extends BaseValidationHelper {
    BillingTestDataProvider testDataProvider = new BillingTestDataProvider();

    public BillingValidationHelper() {
        super();
    }

    public void validateBalance(String uid, String balanceId, String orderId, boolean isTentative) throws Exception {
        Balance balanceResult = Master.getInstance().getBalance(balanceId);
        Order order = Master.getInstance().getOrder(orderId);

        if (isTentative) {
            verifyEqual(balanceResult.getStatus(), BalanceStatus.AWAITING_PAYMENT.toString(), "verify balance status");
        } else {
            //verifyEqual(balanceResult.getStatus(), BalanceStatus.COMPLETED.toString(), "verify balance status");
            verifyEqual(balanceResult.getStatus(), BalanceStatus.AWAITING_PAYMENT.toString(), "verify balance status");
        }

        verifyEqual(balanceResult.getUserId().getValue(),
                IdConverter.hexStringToId(UserId.class, uid), "verify user id");

        verifyEqual(balanceResult.getPiId().getValue(),
                order.getPayments().get(0).getPaymentInstrument().getValue(), "verify payment id");

        verifyEqual(balanceResult.getBalanceItems().size(), order.getOrderItems().size(), "verify balance item size");
        verifyEqual(balanceResult.getType(), BalanceType.DEBIT.toString(), "verify balance type");


        for (int i = 0; i < balanceResult.getBalanceItems().size(); i++) {
            boolean isExist = false;
            for (int j = 0; j < order.getOrderItems().size(); j++) {
                String orderItemId = testDataProvider.getOrderItemId(uid, IdConverter.hexStringToId(OrderId.class,
                        orderId), order.getOrderItems().get(j).getOffer().getValue());
                if (balanceResult.getBalanceItems().get(i).getOrderItemId().getValue().toString()
                        .equals(orderItemId)) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                throw new TestException(String.format("missing order item %s",
                        balanceResult.getBalanceItems().get(i).getOrderItemId().getValue()));
            }
        }


        if (balanceResult.getTransactions().size() > 0) {
            Transaction transaction = balanceResult.getTransactions().get(0);
            verifyEqual(transaction.getPiId().getValue(),
                    order.getPayments().get(0).getPaymentInstrument().getValue(), "verify pid");
            verifyEqual(transaction.getType(), String.format("CHARGE"), "verify transaction type");
            verifyEqual(transaction.getStatus(), String.format("SUCCESS"), "verify transaction status");
            verifyEqual(transaction.getAmount(),
                    order.getTotalAmount().add(order.getTotalTax()), "verify transaction total amount");
            verifyEqual(transaction.getCurrency(), order.getCurrency().getValue(), "verify transaction currency");
        } else {
            throw new TestException("missing billing transactions");
        }

    }

    public void validateBalanceQuote(String uid, String fakeBalanceId, String paymentId) {
        Balance balanceResult = Master.getInstance().getBalance(fakeBalanceId);

        verifyEqual(balanceResult.getUserId().getValue(),
                IdConverter.hexStringToId(UserId.class, uid), "verify user id");

        verifyEqual(balanceResult.getPiId().getValue(),
                IdConverter.hexStringToId(PaymentInstrumentId.class, paymentId), "verify payment id");

        verifyEqual(balanceResult.getType(), BalanceType.DEBIT.toString(), "verify balance type");
        verifyEqual(balanceResult.getTotalAmount(), new BigDecimal(10.83), "verify total amount");
        verifyEqual(balanceResult.getTaxAmount(), new BigDecimal(0.83), "verify tax amount");
        verifyEqual(balanceResult.getTaxStatus(), TaxStatus.TAXED.toString(), "verify tax status");
        verifyEqual(balanceResult.getCountry(), Country.DEFAULT.toString(), "verify country");
        verifyEqual(balanceResult.getCurrency(), Currency.DEFAULT.toString(), "verify currency");
        BalanceItem balanceItem = balanceResult.getBalanceItems().get(0);
        verifyEqual(balanceItem.getAmount(), new BigDecimal(10), "verify balance item amount");
        verifyEqual(balanceItem.getTaxAmount(), new BigDecimal(0.83), "verify balance item tax amount");
        BigDecimal taxAmount = new BigDecimal(0);
        BigDecimal taxRate = new BigDecimal(0);
        for (TaxItem taxItem : balanceItem.getTaxItems()) {
            taxAmount = taxAmount.add(taxItem.getTaxAmount());
            taxRate = taxRate.add(taxItem.getTaxRate());
        }
        verifyEqual(taxAmount, new BigDecimal(0.83), "verify total tax amount");
        verifyEqual(taxRate, new BigDecimal(0.0825), "verify total tax rate");
    }

}
