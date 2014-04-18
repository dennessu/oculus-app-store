package com.junbo.fulfilment.db;

import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    protected long generateLong() {
        return idGenerator.nextId(0);
    }
}
