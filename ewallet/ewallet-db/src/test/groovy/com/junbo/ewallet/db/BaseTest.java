/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db;

import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;

/**
 * base test for db.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    @Override
    @Autowired
    @Qualifier("ewalletDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Autowired
    @Qualifier("idGenerator")
    protected IdGenerator idGenerator;
}
