/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.jobs.paypal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Paypal Reconcile Job.
 */
public class PaypalReconcileJob {
    private PaypalReconcileProcessor reconcileProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaypalReconcileJob.class);

    public void execute() {
        LOGGER.info("Starting paypal settlement job");

        reconcileProcessor.execute();

        LOGGER.info("Paypal settlement job finished");
    }

    @Required
    public void setReconcileProcessor(PaypalReconcileProcessor reconcileProcessor) {
        this.reconcileProcessor = reconcileProcessor;
    }
}
