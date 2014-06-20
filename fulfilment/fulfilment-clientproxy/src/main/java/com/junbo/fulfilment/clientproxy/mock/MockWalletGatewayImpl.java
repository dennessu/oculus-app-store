/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.mock;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.fulfilment.clientproxy.WalletGateway;

/**
 * MockWalletGatewayImpl.
 */
public class MockWalletGatewayImpl implements WalletGateway {
    @Override
    public Transaction credit(CreditRequest request) {
        Transaction trx = new Transaction();
        trx.setId(System.currentTimeMillis());

        return trx;
    }
}
