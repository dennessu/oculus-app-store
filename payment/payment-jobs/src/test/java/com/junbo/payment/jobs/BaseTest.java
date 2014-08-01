package com.junbo.payment.jobs;

import com.junbo.payment.core.provider.ProviderRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public abstract class BaseTest extends AbstractTestNGSpringContextTests {
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    protected ProviderRoutingService providerRoutingService;

    protected UUID generateUUID() {
        return UUID.randomUUID();
    }

    @BeforeTest
    @SuppressWarnings("deprecation")
    public void setup() {

    }

    @AfterTest
    @SuppressWarnings("deprecation")
    public void cleanup() {

    }
}
