/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xmchen on 14-4-2.
 */
public class OrderJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderJob.class);

    protected void execute() {
        LOGGER.info("Starting order job");



        LOGGER.info("Order job finished");
    }
}
