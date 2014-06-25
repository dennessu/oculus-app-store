/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.AuthorizeServiceImpl;
import com.junbo.billing.core.service.BalanceService;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeClass;

import java.util.UUID;

/**
 * Created by xmchen on 14-3-14.
 */

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    protected BalanceService balanceService;

    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    @Autowired
    public void setBalanceService(@Qualifier("mockBalanceService")BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    @Override
    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
    protected void springTestContextPrepareTestInstance() throws Exception {
        super.springTestContextPrepareTestInstance();
        AuthorizeServiceImpl authorizeService = (AuthorizeServiceImpl)applicationContext.getBean(AuthorizeService.class);
        authorizeService.setDisabled(true);
        AuthorizeContext.setAuthorizeDisabled(true);
    }
}
