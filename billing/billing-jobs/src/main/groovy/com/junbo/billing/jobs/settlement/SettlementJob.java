/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs.settlement;

import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xmchen on 14-5-6.
 */
@CompileStatic
public class SettlementJob {

    @Autowired
    private SettlementProcessor settlementProcessor;

    private static final Logger LOGGER = LoggerFactory.getLogger(SettlementJob.class);

    public void execute() {
        LOGGER.info("Starting billing settlement job");

        settlementProcessor.processBalances();

        LOGGER.info("Billing settlement job finished");
    }
}
