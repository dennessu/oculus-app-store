/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by fzhang@wan-san.com on 2/13/14.
 */
@IdResourcePath(value = "/users/{userId}/carts/{0}",
                resourceType = "carts",
                regex = "/users/(?<userId>[0-9A-Za-z]+)/carts/(?<id>[0-9A-Za-z]+)")
public class CartId extends CloudantId {

    public CartId() {}
    public CartId(String value) {
        super(value);
    }
}
