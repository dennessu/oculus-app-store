package com.junbo.payment.clientproxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.IdGenerator;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    protected long getLongId() {
        //shard id
        return 520142848L;
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }
}
