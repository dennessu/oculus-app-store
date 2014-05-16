/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * strong type token order id.
 */
@IdResourcePath(value = "/tokens/orders/{0}", regex = "/tokens/orders/(?<id>[0-9A-Z]+)")
public class TokenOrderId extends Id{
    public TokenOrderId() {}

    public TokenOrderId(Long value) {
        super(value);
    }
}
