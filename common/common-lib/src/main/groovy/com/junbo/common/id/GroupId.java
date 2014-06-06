/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/groups/{0}", regex = "/groups/(?<id>[0-9A-Z]+)")
public class GroupId extends Id {
    public GroupId(){

    }

    public GroupId(Long value) {
        super(value);
    }
}
