/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification;

import com.junbo.notification.queue.TestPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * QueueTest.
 */
public class QueueTest extends BaseTest {
    @Autowired
    private TestPublisher publisher;

    @Test
    public void testBVT() {
        publisher.send("hello baby!");
    }
}
