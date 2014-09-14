/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.pool;

import com.junbo.sharding.hibernate.ShardScope;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;

import javax.transaction.TransactionManager;
import java.util.List;

/**
 * Java doc for PoolTest.
 */
@ContextConfiguration(locations = {"classpath:spring/sharding-context-test.xml"})
public class PoolTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("testHikariSessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private TransactionManager transactionManager;

    //@Test
    public void testBVT() throws Exception {
        transactionManager.begin();

        ShardScope shardScope = new ShardScope(0, 0);
        Session session = sessionFactory.getCurrentSession();
        Assert.assertNotNull(session, "Session should not be null.");

        List<Object> result = session.createSQLQuery("select 1 from dummy_test;").list();
        Assert.assertNotNull(result, "Result should not be null.");

        shardScope.close();

        transactionManager.rollback();
    }
}
