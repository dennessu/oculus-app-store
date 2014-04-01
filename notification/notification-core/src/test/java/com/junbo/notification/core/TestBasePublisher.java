/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.Test;

/**
 * TestBasePublisher.
 */
public class TestBasePublisher extends BaseTest {
    @Autowired
    @Qualifier("emailPublisher")
    private EmailPublisher emailPublisher;

    @Test(expectedExceptions = NotificationException.class)
    public void testPublishWithoutTransactionScope() throws Exception {
        emailPublisher.send("hello baby");
    }

    @Test
    public void testPublishInTransactionScope() throws Exception {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus status) {
                emailPublisher.send("hello baby");

                return null;
            }
        });
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testPublishWithTransactionRollback() throws Exception {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        template.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus status) {
                emailPublisher.send("come on baby");

                throw new RuntimeException("oops, error occurred...");
            }
        });
    }
}
