/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.core.context.WalletContext;
import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * WalletHandler.
 */
public class WalletHandler extends HandlerSupport<WalletContext> {
    @Override
    protected String handle(WalletContext context, FulfilmentAction action) {
        List<Long> success = new ArrayList<>();

        for (LinkedEntry entry : action.getItems()) {
            Item item = catalogGateway.getItem(entry.getId(), entry.getTimestamp());

            CreditRequest request = new CreditRequest();

            request.setUserId(context.getUserId());
            request.setAmount(item.getEwalletAmount());
            request.setCreditType(item.getEwalletCurrencyType());
            request.setCurrency(item.getEwalletCurrency());

            Wallet wallet = walletGateway.credit(request);
            success.add(wallet.getTransactions().get(Constant.UNIQUE_RESULT).getTransactionId());
        }

        return Arrays.toString(success.toArray());
    }
}
