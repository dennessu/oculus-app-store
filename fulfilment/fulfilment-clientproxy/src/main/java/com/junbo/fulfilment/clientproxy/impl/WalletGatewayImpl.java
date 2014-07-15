/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.fulfilment.clientproxy.WalletGateway;
import com.junbo.fulfilment.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * WalletGatewayImpl.
 */
public class WalletGatewayImpl implements WalletGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletGatewayImpl.class);

    @Autowired
    @Qualifier("walletClient")
    private WalletResource walletResource;

    @Override
    public Transaction credit(CreditRequest request) {
        try {
            return walletResource.credit(request).syncGet();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Wallet] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Wallet").exception();
        }
    }
}
