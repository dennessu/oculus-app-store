/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.fulfilment.core.context.WalletContext;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * WalletHandler.
 */
public class WalletHandler extends HandlerSupport<WalletContext> {
    @Override
    protected String handle(WalletContext context, FulfilmentAction action) {
        List<Long> success = new ArrayList<>();

        for (LinkedEntry entry : action.getItems()) {
            Item item = catalogGateway.getItem(entry.getId(), action.getTimestamp());

            CreditRequest request = new CreditRequest();

            request.setTrackingUuid(UUID.randomUUID());
            request.setUserId(context.getUserId());
            request.setCurrency(item.getWalletCurrency());

            // aggregate credit amount
            request.setAmount(item.getWalletAmount().multiply(new BigDecimal(action.getCopyCount())));

            Transaction transaction = walletGateway.credit(request);
            success.add(transaction.getTransactionId());
        }

        return Arrays.toString(success.toArray());
    }
}
