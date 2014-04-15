/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.db.dao;

import com.junbo.cart.db.util.Generator;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fzhang@wan-san.com on 14-1-21.
 */
@ContextConfiguration(locations = {"classpath:context-dao-test.xml"})
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class DaoTestBase extends AbstractTestNGSpringContextTests {

    @Autowired
    protected Generator testGenerator;

    @Autowired
    protected IdGeneratorFacade idGenerator;

}
