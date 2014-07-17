package com.junbo.fulfilment.core;

import com.junbo.fulfilment.common.util.Callback;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    private static final Void NO_RETURN = null;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void executeInNewTransaction(final Callback callback) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        template.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus status) {
                callback.apply();
                return NO_RETURN;
            }
        });
    }

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {

    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {

    }

    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    protected long generateLong() {
        return idGenerator.nextId(0);
    }

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }
}
