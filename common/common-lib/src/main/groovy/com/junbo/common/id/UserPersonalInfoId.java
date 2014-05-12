/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/24/14.
 */
@IdResourcePath("/personal-info/{0}")
public class UserPersonalInfoId extends Id {
    public UserPersonalInfoId() {}
    public UserPersonalInfoId(long value) {
        super(value);
    }
}
