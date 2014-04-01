/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * BasePublisher.
 */
public abstract class TransactionalPublisher extends BasePublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalPublisher.class);

    protected void publish(final Event event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new NotificationException(
                    String.format("Failed to send event [%s] since it is not in a transaction scope.", event.getId()));
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronizationAdapter.STATUS_COMMITTED) {
                    LOGGER.warn("Failed to send event [{}] since the transaction is not committed.", event.getId());

                    return;
                }

                template.send(event);
            }
        });

        LOGGER.info("Register TransactionSynchronization for event [{}] successfully.", event.getId());
    }
}
