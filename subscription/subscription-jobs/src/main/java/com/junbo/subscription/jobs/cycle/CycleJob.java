/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.jobs.cycle;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
//import java.util.logging.Logger;

/**
 * cycle job.
 */
public class CycleJob extends QuartzJobBean {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CycleJob.class);
    @Autowired
    private CycleProcesser cycleProcesser;

    protected void executeInternal(JobExecutionContext arg0)
            throws JobExecutionException {
        LOGGER.info("Starting Cycle Charge Job");
        cycleProcesser.CycleCharge();

    }
}
