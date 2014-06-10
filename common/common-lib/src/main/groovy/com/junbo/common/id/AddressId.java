/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by xmchen on 14-4-15.
 */
@IdResourcePath(value = "/addresses/{0}",
                resourceType = "addresses",
                regex = "/addresses/(?<id>[0-9A-Za-z]+)")
public class AddressId extends Id {

    public AddressId() {}

    public AddressId(Long value) {
        super(value);
    }
}
