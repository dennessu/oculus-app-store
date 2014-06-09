/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/fulfilments/{0}",
                resourceType = "fulfilments",
                regex = "/fulfilments/(?<id>[0-9A-Z]+)")
public class FulfilmentId extends Id {

    public FulfilmentId() {}
    public FulfilmentId(long value) {
        super(value);
    }
}
