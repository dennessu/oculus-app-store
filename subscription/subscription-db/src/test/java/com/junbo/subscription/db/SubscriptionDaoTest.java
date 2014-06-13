/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.db;

import com.junbo.sharding.IdGenerator;
import com.junbo.subscription.db.dao.SubscriptionDao;
import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import com.junbo.subscription.spec.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Subscription dao test.
 */
@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class SubscriptionDaoTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionDao dao;

    @Test
    public void testGet() {
        SubscriptionEntity entity = buildSubscriptionEntity();
        dao.insert(entity);

        Assert.assertNotNull(dao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testInsert() {
        Subscription subscription = buildSubscription();
        Subscription insertedSubscription = subscriptionRepository.insert(subscription);
        Assert.assertEquals(insertedSubscription.getUserId(), subscription.getUserId());
    }


    private Subscription buildSubscription() {
        Subscription subscription = new Subscription();

        subscription.setTrackingUuid(UUID.randomUUID());
        subscription.setUserId(idGenerator.nextId());
        subscription.setPaymentMethodId(new Random().nextLong());
        subscription.setOfferId(String.valueOf(idGenerator.nextId()));
        subscription.setSubsStartDate(new Date(114, 0, 22));
        subscription.setSubsEndDate(new Date(114, 0, 28));
        subscription.setCountry("US");
        subscription.setCurrency("USD");

        subscription.setStatus(SubscriptionStatus.ENABLED.toString());
        return subscription;
    }


    private SubscriptionEntity buildSubscriptionEntity() {
        SubscriptionEntity entity = new SubscriptionEntity();

        entity.setTrackingUuid(UUID.randomUUID());
        entity.setUserId(idGenerator.nextId());
        entity.setPiId(new Random().nextLong());
        entity.setItemId("FreeSubsOffer");
        entity.setSubsStartDate(new Date(114, 0, 22));
        entity.setSubsEndDate(new Date(114, 0, 28));

        entity.setStatusId(SubscriptionStatus.ENABLED);
        entity.setCreatedBy("TEST");
        entity.setCreatedTime(new Date());
        return entity;
    }
}
