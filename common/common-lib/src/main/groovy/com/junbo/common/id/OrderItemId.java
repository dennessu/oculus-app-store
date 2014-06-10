/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for OrderItemId.
 */
@IdResourcePath(value = "/order-items/{0}",
                resourceType = "order-items",
                regex = "/order-items/(?<id>[0-9A-Za-z]+)")
public class OrderItemId extends Id {

    public OrderItemId() {}

    public OrderItemId(long value) {
        super(value);
    }
}
