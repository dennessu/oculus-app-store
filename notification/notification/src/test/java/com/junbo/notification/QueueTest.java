/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * QueueTest.
 */
public class QueueTest extends BaseTest {
    @Autowired
    private TestProducer producer;

    @Test
    public void testBVT() {
        producer.send("hello baby!");
    }
}
