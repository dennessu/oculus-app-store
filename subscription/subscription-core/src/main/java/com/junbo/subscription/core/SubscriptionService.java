/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core;

import com.junbo.subscription.spec.model.Subscription;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * subscription service.
 */
@Transactional
public interface SubscriptionService {
    Subscription getSubscription(Long subscriptionId);

    Subscription addSubscription(Subscription subscription);

    Subscription getSubsByTrackingUuid(Long userId, UUID trackingUuid);
}
