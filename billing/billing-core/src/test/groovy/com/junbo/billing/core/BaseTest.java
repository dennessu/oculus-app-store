/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core;

import com.junbo.billing.core.service.BalanceService;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import java.util.UUID;

/**
 * Created by xmchen on 14-3-14.
 */

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
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
}
