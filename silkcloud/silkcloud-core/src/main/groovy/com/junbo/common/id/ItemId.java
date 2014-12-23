/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/items/{0}",
                resourceType = "items",
                regex = "/items/(?<id>[0-9A-Za-z]+)")
public class ItemId extends CloudantId {
    public ItemId(){

    }

    public ItemId(String value) {
        super(value);
    }
}
