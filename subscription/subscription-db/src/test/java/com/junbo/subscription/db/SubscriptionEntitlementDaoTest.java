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
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;

/**
 * Created by Administrator on 14-4-4.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public class SubscriptionEntitlementDaoTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    private SubscriptionEntitlementRepository subscriptionEntitlementRepository;

    @Autowired
    private SubscriptionEntitlementDao dao;

    @Override
    @Autowired
    @Qualifier("subscriptionDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }


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
