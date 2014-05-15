/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/3/14.
 */
@IdResourcePath(value = "/pii/{0}", regex = "/pii/(?<id>[0-9A-Z]+)")
public class UserPiiId extends Id {
    public UserPiiId() {}
    public UserPiiId(long value) {
        super(value);
    }
}
