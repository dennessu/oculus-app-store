/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.ewallet.spec.resource.WalletResource;
import com.junbo.fulfilment.clientproxy.WalletGateway;
import com.junbo.fulfilment.spec.error.AppErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WalletGatewayImpl.
 */
public class WalletGatewayImpl implements WalletGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletGatewayImpl.class);

    @Autowired
    private WalletResource walletResource;

    @Override
    public Wallet credit(CreditRequest request) {
        try {
            return walletResource.credit(request).wrapped().get();
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Wallet] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("wallet").exception();
        }
    }
}
