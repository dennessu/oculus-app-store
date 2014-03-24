/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification;

import com.junbo.notification.queue.TestProducer;
import com.junbo.notification.topic.TestPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * TopicTest.
 */
public class TopicTest extends BaseTest {
    @Autowired
    private TestPublisher publisher;

    @Test
    public void testBVT() {
        publisher.send("come on baby!");
    }
}
