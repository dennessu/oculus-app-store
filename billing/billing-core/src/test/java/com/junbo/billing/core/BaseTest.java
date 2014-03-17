/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Created by xmchen on 14-3-14.
 */

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {

    protected long generateId() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            //ignore
        }

        return System.currentTimeMillis();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    @Qualifier("billingDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
}
