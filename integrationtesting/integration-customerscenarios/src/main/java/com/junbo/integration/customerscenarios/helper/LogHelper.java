/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jason on 3/7/14.
 */
public class LogHelper {

    private Logger logger;

    public LogHelper(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    public LogHelper(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logError(String message) {
        logger.error(message);
    }

    public void logWarn(String message) {
        logger.warn(message);
    }

}
