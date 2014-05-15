/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/promotions/{0}", regex = "/promotions/(?<id>[0-9A-Z]+)")
public class PromotionId extends Id {
    public PromotionId(){

    }

    public PromotionId(Long value) {
        super(value);
    }
}
