/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.notification;

import com.junbo.notification.core.NotificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * TestBasePublisher.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class TestBasePublisher extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("emailPublisher")
    private EmailPublisher emailPublisher;

    @Test(expectedExceptions = NotificationException.class, enabled = false)
    public void testPublishWithoutTransactionScope() throws Exception {
        emailPublisher.send("hello baby");
    }
}
