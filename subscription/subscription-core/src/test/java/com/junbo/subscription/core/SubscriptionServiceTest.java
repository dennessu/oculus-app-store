package com.junbo.subscription.core;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;
import org.junit.internal.runners.statements.ExpectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class SubscriptionServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    @Override
    @Autowired
    @Qualifier("subscriptionDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Autowired
    private SubscriptionService subscriptionService;

    @Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription.setTrackingUuid(UUID.randomUUID());
        subscription.setUserId(123L);
        subscription.setOfferId(1L);
        subscription = subscriptionService.addSubscription(subscription);

        Assert.assertEquals(subscription.getUserId(), subscriptionService.getSubscription(subscription.getId()).getUserId());

    }


}
