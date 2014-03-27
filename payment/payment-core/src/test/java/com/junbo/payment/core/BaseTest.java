package com.junbo.payment.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    @Override
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("paymentDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    protected long generateLong() {
        return System.currentTimeMillis();
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }
}
