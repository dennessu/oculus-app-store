/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/promotions/{0}",
                resourceType = "promotions",
                regex = "/promotions/(?<id>[0-9A-Za-z]+)")
public class PromotionId extends CloudantId {
    public PromotionId(){

    }

    public PromotionId(String value) {
        super(value);
    }
}
