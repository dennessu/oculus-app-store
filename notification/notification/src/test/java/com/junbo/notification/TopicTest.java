/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification;

import com.junbo.notification.topic.TestPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * TopicTest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test-topic.xml"})
public class TopicTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private TestPublisher publisher;

    @Test
    public void testBVT() {
        publisher.send("come on baby!");
    }
}
