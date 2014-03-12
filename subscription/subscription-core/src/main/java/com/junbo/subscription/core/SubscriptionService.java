/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;


/**
 * subscription service.
 */
public interface SubscriptionService {
    Subscription getsubscription(Long subscriptionId);

    Long addsubscription(SubscriptionEntity subscriptionEntity);
}
