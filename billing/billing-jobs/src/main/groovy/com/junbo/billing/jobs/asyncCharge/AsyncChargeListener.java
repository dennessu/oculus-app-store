/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs.asyncCharge;

import com.junbo.billing.jobs.BillingFacade;
import com.junbo.billing.spec.model.Balance;
import com.junbo.common.id.BalanceId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.notification.core.BaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xmchen on 14-4-21.
 */
public class AsyncChargeListener extends BaseListener {

    @Autowired
    private BillingFacade billingFacade;

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncChargeListener.class);

    protected void onTextMessage(final String eventId, final String message) {
        LOGGER.info("Receive a message with event id: " + eventId);
        Long balanceId;
        try {
            balanceId = Long.parseLong(message);
        } catch(NumberFormatException ex) {
            throw ex;
        }

        LOGGER.info("Sending async charge process request for balance id: " + balanceId);
        Balance balance = new Balance();
        balance.setBalanceId(new BalanceId(balanceId));

        billingFacade.processAsyncBalance(balance).recover(new Promise.Func<Throwable, Promise<Balance>>() {
            @Override
            public Promise<Balance> apply(Throwable throwable) {
                LOGGER.error("Error in processing async charge balance", throwable);
                return Promise.pure(null);
            }
        }).then(new Promise.Func<Balance, Promise<Balance>>() {
            @Override
            public Promise<Balance> apply(Balance balance) {
                if(balance == null) {
                    return Promise.pure(null);
                }
                LOGGER.info("The processed balance status is " + balance.getStatus() + "for balance id: " +
                        balance.getBalanceId().getValue());
                return Promise.pure(balance);
            }
        });
    }
}
