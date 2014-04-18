/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.fulfilment.clientproxy.WalletGateway;

import java.util.ArrayList;

/**
 * MockWalletGatewayImpl.
 */
public class MockWalletGatewayImpl implements WalletGateway {
    @Override
    public Wallet credit(CreditRequest request) {
        Wallet wallet = new Wallet();

        final Transaction trx = new Transaction();
        trx.setTransactionId(12345L);

        wallet.setTransactions(new ArrayList<Transaction>() {{
            add(trx);
        }});

        return wallet;
    }
}
