/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core.event;

import com.junbo.subscription.core.SubscriptionEventService;
import com.junbo.subscription.core.action.ChargeAction;
import com.junbo.subscription.db.entity.SubscriptionEventType;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionEventRepository;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 14-5-21.
 */
public class SubscriptionCycleEvent implements SubscriptionEventService {
    @Autowired
    private SubscriptionEventRepository subscriptionEventRepository;

    @Autowired
    private ChargeAction chargeAction;
    @Override
    public Subscription execute(Subscription subscription){
        SubscriptionEvent event = new SubscriptionEvent();
        event.setSubscriptionId(subscription.getSubscriptionId());
        event.setRetryCount(0);
        event.setEventStatus(SubscriptionStatus.CREATED.toString());
        event.setEventTypeId(SubscriptionEventType.CYCLED.toString());
        event = subscriptionEventRepository.insert(event);

        chargeAction.execute(subscription, event);
        return  subscription;
    }

}
