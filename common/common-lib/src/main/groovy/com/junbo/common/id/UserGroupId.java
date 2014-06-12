/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/user-group-memberships/{0}",
                resourceType = "user-group-memberships",
                regex = "/user-group-memberships/(?<id>[0-9A-Za-z]+)")
public class UserGroupId extends CloudantId {
    public UserGroupId(){
    }

    public UserGroupId(String value) {
        super(value);
    }
}
