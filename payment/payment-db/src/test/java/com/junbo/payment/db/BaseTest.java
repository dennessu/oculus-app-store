package com.junbo.payment.db;

import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    protected static final Long userId = 1493188608L;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;
    @Autowired
    protected PlatformTransactionManager transactionManager;

    protected long generateShardId(long masterId) {
        return idGenerator.nextId(masterId);
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    protected long generateLong() {
        return System.currentTimeMillis();
    }
}
