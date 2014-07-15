/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.langur.core.promise.ExecutorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * BaseTest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected MegaGateway megaGateway;

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {
        ExecutorContext.setAsyncMode(false);
    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {
        ExecutorContext.resetAsyncMode();
    }
}