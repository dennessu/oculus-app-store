/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xin.
 */
@IdResourcePath(value = "/user-logs/{0}",
                resourceType = "user-logs",
                regex = "/user-logs/(?<id>[0-9A-Za-z]+)")
public class UserLogId extends CloudantId {
    public UserLogId(){
    }

    public UserLogId(String value) {
        super(value);
    }
}
