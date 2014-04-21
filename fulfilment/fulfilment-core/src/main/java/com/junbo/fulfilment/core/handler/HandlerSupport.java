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
import com.junbo.fulfilment.core.FulfilmentHandler;
import com.junbo.fulfilment.core.context.FulfilmentContext;
import com.junbo.fulfilment.core.service.TransactionSupport;
import com.junbo.fulfilment.db.repo.FulfilmentActionRepository;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * HandlerSupport.
 *
 * @param <T> fulfilment context type
 */
public abstract class HandlerSupport<T extends FulfilmentContext>
        extends TransactionSupport
        implements FulfilmentHandler<T> {
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

    public void process(T context) {
        for (final FulfilmentAction action : context.getActions()) {
            try {
                action.setResult(handle(context, action));
                action.setStatus(FulfilmentStatus.SUCCEED);
            } catch (Exception e) {
                action.setStatus(FulfilmentStatus.FAILED);
            }

            executeInNewTransaction(new Callback() {
                public void apply() {
                    updateAction(action.getActionId(), action.getStatus(), action.getResult());
                }
            });
        }
    }

    protected String handle(T context, FulfilmentAction action) {
        throw new RuntimeException("not implemented");
    }
}
