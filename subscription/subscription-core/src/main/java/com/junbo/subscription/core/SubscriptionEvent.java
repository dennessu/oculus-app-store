/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core;

import com.junbo.subscription.spec.model.Subscription;

/**
 * Created by Administrator on 14-3-28.
 */
public interface SubscriptionEvent {
    Subscription excute(Subscription subscription);
}
