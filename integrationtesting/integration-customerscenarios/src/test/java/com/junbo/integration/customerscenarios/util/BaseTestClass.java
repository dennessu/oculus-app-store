/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 @author Jason
  * Time: 3/7/2014
  * Base test class
 */
public class BaseTestClass {

    public BaseTestClass() {
        //set loggging info
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SLF4JLogger");
        System.setProperty("logback.configurationFile", "logback-test.xml");
    }

}
