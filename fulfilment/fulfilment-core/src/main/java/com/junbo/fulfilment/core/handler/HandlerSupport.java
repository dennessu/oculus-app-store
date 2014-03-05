/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.clientproxy.BillingGateway;
import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.clientproxy.EntitlementGateway;
import com.junbo.fulfilment.clientproxy.WalletGateway;
import com.junbo.fulfilment.common.util.Callback;
import com.junbo.fulfilment.core.service.TransactionSupport;
import com.junbo.fulfilment.db.repo.FulfilmentActionRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HandlerSupport.
 */
public abstract class HandlerSupport extends TransactionSupport {
    @Autowired
    protected CatalogGateway catalogGateway;

    @Autowired
    protected BillingGateway billingGateway;

    @Autowired
    protected EntitlementGateway entitlementGateway;

    @Autowired
    protected WalletGateway walletGateway;

    @Autowired
    protected FulfilmentActionRepository actionRepo;

    protected void updateAction(final Long actionId, final String status, final String result) {
        executeInNewTransaction(new Callback() {
            public void apply() {
                actionRepo.update(actionId, status, result);
            }
        });
    }
}
