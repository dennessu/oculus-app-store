/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xmchen on 14-4-4.
 */
public class AsyncChargeJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncChargeJob.class);

    protected void execute() {
        LOGGER.info("Starting billing async charge job");



        LOGGER.info("Billing async charge job finished");
    }
}
