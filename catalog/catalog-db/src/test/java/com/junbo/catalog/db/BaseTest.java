/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Created by baojing on 2/18/14.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    /**
     * <p>Simple entity id generator.</p>
     *
     * @return new generated entity id
     */
    protected long generateId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }
}
