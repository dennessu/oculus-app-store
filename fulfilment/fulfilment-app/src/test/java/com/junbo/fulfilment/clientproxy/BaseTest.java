/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * BaseTest.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected MegaGateway megaGateway;
}