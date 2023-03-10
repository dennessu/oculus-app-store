/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core.event;

import com.junbo.subscription.core.SubscriptionEventService;
import com.junbo.subscription.spec.model.Subscription;

/**
 * Cancel Subscription.
 */
public class SubscriptionCancelEvent implements SubscriptionEventService {
    @Override
    public Subscription execute(Subscription subscription){
        return  subscription;
    }
}
