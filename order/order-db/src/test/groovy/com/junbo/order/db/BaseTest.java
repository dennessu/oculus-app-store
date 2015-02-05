/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db;

import com.junbo.order.db.common.TestHelper;
import com.junbo.sharding.IdGeneratorFacade;
import com.junbo.sharding.ShardAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by LinYi on 2/11/14.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected IdGeneratorFacade idGenerator;

    @Autowired
    @Qualifier("userShardAlgorithm")
    protected ShardAlgorithm shardAlgorithm;

    /**
     * <p>Simple entity id generator.</p>
     *
     * @return new generated entity id
     */
    protected long generateId() {
        return TestHelper.generateId();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    protected long generateLong() {
        return System.currentTimeMillis();
    }
}
