/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/orders/{0}",
                resourceType = "orders",
                regex = "/order/(?<id>[0-9A-Za-z]+)")
public class OrderId extends Id {

    public OrderId() {}

    public OrderId(Long value) {
        super(value);
    }
}
