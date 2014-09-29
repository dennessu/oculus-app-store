/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/categories/{0}",
                resourceType = "categories",
                regex = "/categories/(?<id>[0-9A-Za-z]+)")
public class CategoryId extends CloudantId {
    public CategoryId(){

    }

    public CategoryId(String value) {
        super(value);
    }
}
