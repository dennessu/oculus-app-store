/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/categories/{0}", regex = "/categories/(?<id>[0-9A-Z]+)")
public class CategoryId extends Id {
    public CategoryId(){

    }

    public CategoryId(Long value) {
        super(value);
    }
}
