/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.common.id.UserId;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    protected IdGeneratorFacade idGeneratorFacade;

    protected long generateUserId() {
        return idGeneratorFacade.nextId(UserId.class);
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
