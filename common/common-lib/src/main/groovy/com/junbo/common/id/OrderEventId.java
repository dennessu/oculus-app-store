/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for OrderEventId.
 */
@IdResourcePath(value = "/order-events/{0}",
                resourceType = "order-events",
                regex = "/order-events/(?<id>[0-9A-Za-z]+)")
public class OrderEventId extends Id {

    public OrderEventId() {}

    public OrderEventId(Long value) {
        super(value);
    }
}
