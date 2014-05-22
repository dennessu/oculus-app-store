package com.junbo.subscription.core;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;
//import org.junit.internal.runners.statements.ExpectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional("transactionManager")
public class SubscriptionServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription.setTrackingUuid(UUID.randomUUID());
        subscription.setUserId(1493188608L);
        subscription.setOfferId(1L);
        Subscription subs = subscriptionService.addSubscription(subscription);

        Assert.assertEquals(subscription.getUserId(), subscriptionService.getSubscription(subs.getId()).getUserId());

    }


}
