/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by liangfu on 3/9/14.
 */
@IdResourcePath(value = "/offers/{0}",
                resourceType = "offers",
                regex = "/offers/(?<id>[0-9A-Za-z]+)")
public class OfferId extends CloudantId {
    public OfferId(){

    }

    public OfferId(String value) {
        super(value);
    }
}
