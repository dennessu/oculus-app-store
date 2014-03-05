package com.junbo.fulfilment.db;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    protected long generateLong() {
        return System.currentTimeMillis();
    }
}
