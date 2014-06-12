/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/22/14.
 */
@IdResourcePath(value = "/users/{userId}/tfa/{0}",
                resourceType = "tfa",
                regex = "/users/(?<userId>[0-9A-Za-z]+)/tfa/(?<id>[0-9A-Za-z]+)")
public class UserTFAId extends CloudantId {

    public UserTFAId() {}
    public UserTFAId(String value) {
        super(value);
    }
}
