/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Java doc for OrderEventId.
 */
@IdResourcePath("/order-events/{0}")
public class OrderEventId extends Id {

    public OrderEventId() {}

    public OrderEventId(long value) {
        super(value);
    }
}
