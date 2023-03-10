/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core;

import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEvent;

/**
 * Created by Administrator on 14-5-21.
 */
public interface SubscriptionActionService {
    Subscription execute(Subscription subscription, SubscriptionEvent event);
}
