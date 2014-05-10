/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.core.context.WalletContext;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * WalletHandler.
 */
public class WalletHandler extends HandlerSupport<WalletContext> {
    @Override
    protected Object handle(WalletContext context, FulfilmentAction action) {
        Map<String, Object> actionProp = action.getProperties();

        CreditRequest request = new CreditRequest();

        request.setTrackingUuid(UUID.randomUUID());
        request.setUserId(context.getUserId());
        request.setCurrency(actionProp.get(Constant.STORED_VALUE_CURRENCY).toString());

        // aggregate credit amount
        BigDecimal amount = (BigDecimal) actionProp.get(Constant.STORED_VALUE_AMOUNT);
        BigDecimal totalCreditAmount = amount
                .multiply(new BigDecimal(action.getCopyCount()));

        request.setAmount(totalCreditAmount);
        Transaction transaction = walletGateway.credit(request);

        return transaction.getTransactionId();
    }
}
