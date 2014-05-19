package com.junbo.email.db

import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration

import javax.sql.DataSource

/**
 * Created by Wei on 5/15/2014.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
@TransactionConfiguration(defaultRollback = true)
abstract class BaseTest extends AbstractTransactionalTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator

    protected UUID generateUUID() {
        return UUID.randomUUID()
    }

    protected long generateLong() {
        return idGenerator.nextId(0)
    }
    @Override
    @Qualifier("emailDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }
}