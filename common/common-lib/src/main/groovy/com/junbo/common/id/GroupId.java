/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/groups/{0}",
                resourceType = "groups",
                regex = "/groups/(?<id>[0-9A-Za-z]+)")
public class GroupId extends CloudantId {

    public GroupId(){

    }

    public GroupId(String value) {
        super(value);
    }
}
