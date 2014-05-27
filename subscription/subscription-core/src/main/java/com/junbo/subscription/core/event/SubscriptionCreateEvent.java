/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.event;

import com.junbo.subscription.core.SubscriptionEventService;
import com.junbo.subscription.core.action.ChargeAction;
import com.junbo.subscription.core.action.FullfilmentAction;
import com.junbo.subscription.db.entity.SubscriptionEventType;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionEventRepository;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Create Subscription.
 */
public class SubscriptionCreateEvent implements SubscriptionEventService {
    @Autowired
    private SubscriptionEventRepository subscriptionEventRepository;

    @Autowired
    private ChargeAction chargeAction;
    @Autowired
    private FullfilmentAction fullfilmentAction;

    @Override
    public Subscription execute(Subscription subscription){
        SubscriptionEvent event = buildEvent(subscription, SubscriptionEventType.CREATED);
        event = subscriptionEventRepository.insert(event);

        chargeAction.execute(subscription, event);
        fullfilmentAction.execute(subscription, event);

        return  subscription;
    }

    SubscriptionEvent buildEvent(Subscription subscription, SubscriptionEventType type){
        SubscriptionEvent event = new SubscriptionEvent();
        event.setSubscriptionId(subscription.getSubscriptionId());
        event.setRetryCount(0);
        event.setEventStatus(SubscriptionStatus.CREATED.toString());
        event.setEventTypeId(type.toString());
        return event;
    }
}
