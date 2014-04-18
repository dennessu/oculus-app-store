package com.junbo.fulfilment.db;

import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.sql.DataSource;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Override
    @Autowired
    @Qualifier("fulfilmentDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    protected long generateLong() {
        return idGenerator.nextId(0);
    }
}
