/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db;

import com.junbo.subscription.db.dao.SubscriptionEntitlementDao;
import com.junbo.subscription.db.repository.SubscriptionEntitlementRepository;
import com.junbo.subscription.spec.model.SubscriptionEntitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * Created by Administrator on 14-4-4.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class SubscriptionEntitlementDaoTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private SubscriptionEntitlementRepository subscriptionEntitlementRepository;

    @Autowired
    private SubscriptionEntitlementDao dao;

    @Test
    public void testInsert() {
        SubscriptionEntitlement build = buildSubscriptionEntitilement();
        SubscriptionEntitlement insterted = subscriptionEntitlementRepository.insert(build);
        Assert.assertEquals(build.getSubscriptionId(), insterted.getSubscriptionId());
    }

    private SubscriptionEntitlement buildSubscriptionEntitilement(){
        SubscriptionEntitlement subsEnt = new SubscriptionEntitlement();
        subsEnt.setSubscriptionId(1L);
        subsEnt.setEntitlementId(1234L);
        subsEnt.setEntitlementStatus(0);
        return subsEnt;
    }
}
