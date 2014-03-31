package com.junbo.token.clientproxy;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    protected long generateLong() {
        return System.currentTimeMillis();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }
}
