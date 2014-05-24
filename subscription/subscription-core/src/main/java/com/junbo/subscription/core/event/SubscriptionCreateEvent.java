/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.event;

import com.junbo.subscription.core.SubscriptionEvent;
import com.junbo.subscription.core.action.ChargeAction;
import com.junbo.subscription.core.action.FullfilmentAction;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import com.junbo.subscription.spec.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create Subscription.
 */
public class SubscriptionCreateEvent implements SubscriptionEvent {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ChargeAction chargeAction;
    @Autowired
    private FullfilmentAction fullfilmentAction;

    @Override
    public Subscription execute(Subscription subscription){
        subscription.setStatus(SubscriptionStatus.CREATED.toString());
        subscription = subscriptionRepository.insert(subscription);

        chargeAction.execute(subscription);
        fullfilmentAction.execute(subscription);

        subscription.setStatus(SubscriptionStatus.ENABLED.toString());
        subscription = subscriptionRepository.update(subscription);
        subscription.setPaymentMethodId(111L);

        return  subscription;
    }
}
