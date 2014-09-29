/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/24/14.
 */
@IdResourcePath(value = "/personal-info/{0}",
                resourceType = "personal-info",
                regex = "/personal-info/(?<id>[0-9A-Za-z]+)")
public class UserPersonalInfoId extends Id {
    public UserPersonalInfoId() {}
    public UserPersonalInfoId(Long value) {
        super(value);
    }
}
