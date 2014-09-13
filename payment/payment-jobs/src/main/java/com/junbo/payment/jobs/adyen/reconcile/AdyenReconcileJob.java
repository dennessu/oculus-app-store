/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.adyen.reconcile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Reconcile Job.
 */
public class AdyenReconcileJob {
    private AdyenReconcileProcessor reconcileProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenReconcileJob.class);

    public void execute() {
        LOGGER.info("Starting adyen settlement job");

        reconcileProcessor.processPayments();

        LOGGER.info("Adyen settlement job finished");
    }

    @Required
    public void setReconcileProcessor(AdyenReconcileProcessor reconcileProcessor) {
        this.reconcileProcessor = reconcileProcessor;
    }
}
