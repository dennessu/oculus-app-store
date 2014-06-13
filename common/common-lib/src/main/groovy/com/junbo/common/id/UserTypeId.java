/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/17/14.
 */
@IdResourcePath(value = "/user-type/{0}",
                resourceType = "user-type",
                regex = "/user-type/(?<id>[0-9A-Za-z]+)")
public class UserTypeId extends CloudantId {
    public UserTypeId() {}

    public UserTypeId(String value) {
        super(value);
    }
}
