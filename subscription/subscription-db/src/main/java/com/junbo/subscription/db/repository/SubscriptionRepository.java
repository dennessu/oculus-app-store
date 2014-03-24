/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionDao;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.db.entity.SubscriptionEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * subscription repository.
 */
public class SubscriptionRepository {
    @Autowired
    private SubscriptionDao subscriptionDao;
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    public Subscription get(Long subscriptionId) {
        SubscriptionEntity entity;
        try {
            entity = subscriptionDao.get(subscriptionId);
        }
        catch (Exception e){
            return null;
        }
        return subscriptionMapper.toSubscription(entity);
    }

    public Subscription insert(Subscription subscription) {
        Long id = subscriptionDao.insert(subscriptionMapper.toSubscriptionEntity(subscription));
        SubscriptionEntity result = subscriptionDao.get(id);
        return subscriptionMapper.toSubscription(result);

    }

    public Long update(Subscription subscription) {
        return subscriptionDao.update(subscriptionMapper.toSubscriptionEntity(subscription));
    }

    public Subscription getByTrackingUuid(UUID trackingUuid) {
        return subscriptionMapper.toSubscription(subscriptionDao.getByTrackingUuid(trackingUuid));
    }
}
