/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xmchen on 14-4-3.
 */
public class BalanceProcessJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceProcessJob.class);

    protected void execute() {
        LOGGER.info("Starting billing balance process job");



        LOGGER.info("Billing balance process job finished");
    }
}
