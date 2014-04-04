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

import java.util.List;
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

    public Subscription update(Subscription subscription) {
        subscriptionDao.update(subscriptionMapper.toSubscriptionEntity(subscription));
        SubscriptionEntity result = subscriptionDao.get(subscription.getId());
        return subscriptionMapper.toSubscription(result);
    }

    public Subscription getByTrackingUuid(Long userId, UUID trackingUuid) {
        List<SubscriptionEntity> subsList = subscriptionDao.getByTrackingUuid(userId, trackingUuid);
        if(subsList != null && !subsList.isEmpty()){
            return subscriptionMapper.toSubscription(subsList.get(0));
        }
        return null;
    }
}
