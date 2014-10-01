/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.dualwrite.test

import groovy.transform.CompileStatic
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.transaction.annotation.Transactional
/**
 * Java doc for IdGeneratorTest.
 */
@ContextConfiguration(locations = "classpath:spring/sharding-context-test.xml")
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
@CompileStatic
public class PendingActionSingleWriteStrategyTest extends AbstractTestNGSpringContextTests {

    //@Test
    public void testWrite() {
        // TODO:
    }
}
