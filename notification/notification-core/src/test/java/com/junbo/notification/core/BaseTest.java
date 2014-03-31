/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.apache.activemq.broker.BrokerService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * BaseTest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class BaseTest extends AbstractTestNGSpringContextTests {
    private static String brokerUrl;

    private BrokerService broker;

    public static void setBrokerUrl(String brokerUrl) {
        BaseTest.brokerUrl = brokerUrl;
    }

    @BeforeClass
    public void setUp() throws Exception {
        broker = new BrokerService();
        broker.setTransportConnectorURIs(new String[]{"tcp://0.0.0.0:61616"});
        broker.start(true);
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (broker != null && broker.isStarted()) {
            broker.stop();
        }
    }
}
