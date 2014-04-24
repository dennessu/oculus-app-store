/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by fzhang@wan-san.com on 2/13/14.
 */
@IdResourcePath("/users/{userId}/carts/{0}")
public class CartId extends Id {

    public CartId() {}
    public CartId(long value) {
        super(value);
    }
}
