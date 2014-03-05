package com.junbo.fulfilment.core;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    protected long generateLong() {
        return System.currentTimeMillis();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }
}
