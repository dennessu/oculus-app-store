package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionDao;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.PageMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

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
