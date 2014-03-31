/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.notification.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.Test;

/**
 * TestBasePublisher.
 */
public class TestBasePublisher extends BaseTest {
    @Autowired
    @Qualifier("emailPublisher")
    private EmailPublisher emailPublisher;

    @Test
    public void testBVT() throws Exception {
        emailPublisher.send("hello baby");
    }
}
