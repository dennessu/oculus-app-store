/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * QueueTest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test-queue.xml"})
public class QueueTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private TestProducer producer;

    @Test
    public void testBVT() {
        producer.send("hello baby!");
    }
}
