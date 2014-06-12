/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/3/14.
 */
@IdResourcePath(value = "/pii/{0}",
                resourceType = "pii",
                regex = "/pii/(?<id>[0-9A-Za-z]+)")
public class UserPiiId extends CloudantId {
    public UserPiiId() {}
    public UserPiiId(String value) {
        super(value);
    }
}
