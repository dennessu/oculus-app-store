/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfuxia on 3/7/14.
 */
@IdResourcePath(value = "/subscriptions/{0}",
                resourceType = "subscriptions",
                regex = "/subscriptions/(?<id>[0-9A-Za-z]+)")
public class SubscriptionId extends Id {

    public SubscriptionId() {}
    public SubscriptionId(Long value) {
        super(value);
    }
}
