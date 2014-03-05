package com.junbo.subscription.core;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.PageMetadata;
import com.junbo.subscription.spec.model.Subscription;

import java.util.List;

public interface SubscriptionService {
    public Subscription getsubscription(Long subscriptionId);

    public Long addsubscription(SubscriptionEntity subscriptionEntity);
}
