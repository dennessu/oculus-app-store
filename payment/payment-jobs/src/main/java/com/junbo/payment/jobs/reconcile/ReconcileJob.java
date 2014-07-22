/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.reconcile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Reconcile Job.
 */
public class ReconcileJob {
    private ReconcileProcessor reconcileProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconcileJob.class);

    public void execute() {
        LOGGER.info("Starting billing settlement job");

        reconcileProcessor.processPayments();

        LOGGER.info("Billing settlement job finished");
    }

    @Required
    public void setReconcileProcessor(ReconcileProcessor reconcileProcessor) {
        this.reconcileProcessor = reconcileProcessor;
    }
}
