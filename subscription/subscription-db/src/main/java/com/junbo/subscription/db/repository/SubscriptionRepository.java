/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionDao;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * subscription repository.
 */
public class SubscriptionRepository {
    @Autowired
    private SubscriptionDao subscriptionDao;
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    public Subscription get(Long entitlementId) {
        return subscriptionMapper.toSubscription(subscriptionDao.get(entitlementId));
    }

    public Long insert(Subscription subscription) {
        return subscriptionDao.insert(subscriptionMapper.toSubscriptionEntity(subscription));
    }

    public Long update(Subscription subscription) {
        return subscriptionDao.update(subscriptionMapper.toSubscriptionEntity(subscription));
    }
}
