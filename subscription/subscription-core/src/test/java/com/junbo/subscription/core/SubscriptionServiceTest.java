package com.junbo.subscription.core;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;
import org.junit.internal.runners.statements.ExpectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Random;

@ContextConfiguration(locations = {"classpath:spring/context-test.xml"})
public class SubscriptionServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    //@Autowired
    private SubscriptionService subscriptionService;

    //@Test
    public void testAddSubscription() {
        Subscription subscription = new Subscription();
        subscription = subscriptionService.addsubscription(subscription);
    }


}
